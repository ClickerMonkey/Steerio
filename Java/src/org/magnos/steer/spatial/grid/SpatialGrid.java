
package org.magnos.steer.spatial.grid;

import java.util.Iterator;

import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;
import org.magnos.steer.util.LinkedList;
import org.magnos.steer.util.LinkedNode;
import org.magnos.steer.vec.Vec;


// A SpatialDatabase stores SpatialEntitys to be quickly searched and checked for intersections.
// TODO KNN using the cells
// TODO verify accuracy against brute-force method (SpatialArray)
public class SpatialGrid<V extends Vec<V>> implements SpatialDatabase<V>
{

    // A grid of cells, which are a linked list of entities.
    public final SpatialGridCell<V>[] cells;

    // The number of cells wide the database is.
    public final V dimensions;

    // The offset of the database grid, typically at {0,0}
    public final V offset;

    // The size of each cell in the database.
    public final V size;

    // The inverse of size.
    public final V sizeInversed;

    // The vector that calculates a cells index when the dot product is taken with the cell location.
    public final V dimensionsScale;

    // The list of entities outside the grid.
    public final SpatialGridCell<V> outside;

    // The list of entities in the database.
    public final LinkedList<SpatialGridNode<V>> entities;

    // The boundaries of this database.
    public final V min, max;

    public int collisionChecks;
    public int collisionMatches;
    public int collisions;
    public int remapped;

    public SpatialGrid( V dimensions, V offset, V cellSize )
    {
        this.dimensions = dimensions;
        this.dimensionsScale = dimensions.create();
        this.offset = offset;
        this.size = cellSize;
        this.sizeInversed = cellSize.invert();
        this.outside = new SpatialGridCell<V>( offset.create().clear( -1 ), -1, this );
        this.entities = new LinkedList<SpatialGridNode<V>>();
        this.min = offset.clone();
        this.max = dimensions.mul( cellSize ).addi( offset );

        int capacity = 1;
        int scale = 1;
        for ( int i = 0; i < dimensions.size(); i++ )
        {
            dimensionsScale.setComponent( i, scale );
            float dim = dimensions.getComponent( i );
            capacity *= dim;
            scale *= dim;
        }

        this.cells = new SpatialGridCell[capacity];

        for ( int i = 0; i < capacity; i++ )
        {
            cells[i] = new SpatialGridCell<V>( getCellIndexFromIndex( i, offset.create() ), i, this );
        }
    }

    @Override
    public Iterator<SpatialEntity<V>> iterator()
    {
        return null;
    }

    public V getCellIndexFromIndex( int at, V out )
    {
        for ( int i = 0; i < out.size(); i++ )
        {
            float dim = (int)dimensions.getComponent( i );

            out.setComponent( i, at % dim );
            at /= dim;
        }

        return out;
    }

    public int getIndexFromCellIndex( V v )
    {
        for (int i = 0; i < v.size(); i++)
        {
            float c = v.getComponent( i );
            
            if (c < 0 || c >= dimensions.getComponent( i ))
            {
                return -1;
            }
        }
        
        return (int)dimensionsScale.dot( v );
    }

    @Override
    public void add( SpatialEntity<V> entity )
    {
        // Create node for entity
        final SpatialGridNode<V> node = new SpatialGridNode<V>( entity );

        // Add the entity to the database entity list.
        entities.add( node.databaseNode );

        // Assume the node is on the outside, let the update fix it.
        outside.add( node.cellNode );

        // Add them to their cell or the outside list.
        updateNode( node );
    }

    @Override
    public void clear()
    {
        clear( entities );
        clear( outside );

        for ( int i = 0; i < cells.length; i++ )
        {
            clear( cells[i] );
        }
    }

    private void clear( LinkedList<?> list )
    {
        LinkedNode<?> end = list.head;
        LinkedNode<?> start = end.next;

        while ( start != end )
        {
            LinkedNode<?> next = start.next;
            start.remove();
            start = next;
        }
    }

    @Override
    public int refresh()
    {
        // Updates which cell each entity exists in.
        int alive = 0;

        remapped = 0;

        // Clear all look-backs
        for ( int i = 0; i < cells.length; i++ )
        {
            cells[i].lookback.clear();
        }

        LinkedNode<SpatialGridNode<V>> linkedNode = entities.head.next;

        // Update entity nodes
        while ( linkedNode != entities.head )
        {
            final SpatialGridNode<V> node = linkedNode.value;
            final SpatialEntity<V> entity = node.entity;
            final LinkedNode<SpatialGridNode<V>> nextNode = linkedNode.next;

            // If the entity is inert, remove it from the database.
            if ( entity.isInert() )
            {
                node.remove();
            }
            // If they are not static, update their cell.
            else if ( !entity.isStatic() )
            {
                // An entity that's alive!
                alive++;

                // Re-calculate cell index
                if ( updateNode( node ) )
                {
                    remapped++;
                }
            }

            // Update look-back of cells
            updateLookback( entity.getPosition(), entity.getRadius() );

            // Next entity...
            linkedNode = nextNode;
        }

        return alive;
    }

    private boolean updateNode( SpatialGridNode<V> node )
    {
        final SpatialEntity<V> entity = node.entity;
        final LinkedNode<SpatialGridNode<V>> cellNode = node.cellNode;
        final SpatialGridCell<V> cell = node.cell;
        final V pos = entity.getPosition();
        final float rad = entity.getRadius();

        final V corner = pos.add( -rad );
//        final boolean isOutside = !corner.isBetween( min, max, 0 );
        final int index = getIndexFromCellIndex( getCellIndex( corner ) );
        final boolean isOutside = (index == -1);

        if ( ((cell == null) != isOutside) || (cell != null && cell.offset != index) )
        {
            cellNode.remove();

            if ( isOutside )
            {
                outside.add( cellNode );
                node.cell = null;
            }
            else
            {
                cells[index].add( node.cellNode );
                node.cell = cells[index];
            }

            return true;
        }

        return false;
    }

    private void updateLookback( V pos, float rad )
    {
        final V cellIndex = getCellIndex( pos.add( -rad ) );
        final CellSpan span = getCellSpan( pos, rad, false, new CellSpan() );

        span.reset();
        
        do
        {
            SpatialGridCell<V> currentCell = cells[span.index];
            
            currentCell.lookback.maxi( currentCell.lookback, span.current.sub( cellIndex ) );
        }
        while( span.next() );
    }

    @Override
    public int handleCollisions( CollisionCallback<V> callback )
    {
        int collisionCount = 0;

        callback.onCollisionStart();

        LinkedNode<SpatialGridNode<V>> linkedNode = entities.head.next;
        CellSpan span = new CellSpan();

        while ( linkedNode != entities.head )
        {
            final SpatialGridNode<V> node = linkedNode.value;
            final SpatialEntity<V> entity = node.entity;
            final V pos = entity.getPosition();
            final float rad = entity.getRadius();
            final LinkedNode<SpatialGridNode<V>> nextNode = linkedNode.next;

            if ( entity.isInert() )
            {
                continue;
            }

            getCellSpan( pos, rad, false, span );

            span.reset();
            
            do
            {
                SpatialGridCell<V> cell = cells[span.index];

                // For the cell of the entity, start looking after the entity node to avoid duplicate.
                if ( cell == node.cell )
                {
                    collisionCount += handleCollisions( entity, node.cellNode, cell.head, callback );
                }
                // For other cells, check against entire cell.
                else
                {
                    collisionCount += handleCollisions( entity, cell.head, cell.head, callback );
                }

                if ( entity.isInert() )
                {
                    break;
                }
            }
            while ( span.next() );

            // If the current entity is partially outside, check collisions with other entities
            if ( !entity.isInert() && isOutsidePartially( pos, rad ) )
            {
                collisionCount += handleCollisions( entity, outside.head, outside.head, callback );
            }

            // Next entity...
            linkedNode = nextNode;
        }

        callback.onCollisionEnd();

        return collisionCount;
    }

    private int handleCollisions( SpatialEntity<V> a, LinkedNode<SpatialGridNode<V>> start, LinkedNode<SpatialGridNode<V>> end, CollisionCallback<V> callback )
    {
        start = start.next;

        int collisionCount = 0;

        while ( start != end )
        {
            final SpatialEntity<V> entity = start.value.entity;

            if ( !entity.isInert() )
            {
                collisionCount = SpatialUtility.handleCollision( a, entity, collisionCount, callback );

                if ( a.isInert() )
                {
                    break;
                }
            }

            start = start.next;
        }

        return collisionCount;
    }

    @Override
    public int intersects( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback )
    {
        final CellSpan span = getCellSpan( offset, radius, true, new CellSpan() );

        int intersectCount = 0;

        span.reset();
        
        do
        {
            intersectCount = intersects( offset, radius, max, collidesWith, callback, intersectCount, cells[span.index] );

            if ( intersectCount == max )
            {
                break;
            }
        }
        while( span.next() );

        if ( intersectCount < max && isOutsidePartially( offset, radius ) )
        {
            intersectCount = intersects( offset, radius, max, collidesWith, callback, intersectCount, outside );
        }

        return intersectCount;
    }

    private int intersects( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback, int intersectCount, LinkedList<SpatialGridNode<V>> list )
    {
        final LinkedNode<SpatialGridNode<V>> end = list.head;
        LinkedNode<SpatialGridNode<V>> start = end.next;

        while ( start != end )
        {
            final SpatialEntity<V> a = start.value.entity;

            if ( !a.isInert() && (a.getSpatialGroups() & collidesWith) != 0 )
            {
                final float overlap = SpatialUtility.overlap( a, offset, radius );

                if ( overlap > 0 )
                {
                    if ( callback.onFound( a, overlap, intersectCount, offset, radius, max, collidesWith ) )
                    {
                        intersectCount++;

                        if ( intersectCount >= max )
                        {
                            break;
                        }
                    }
                }
            }

            start = start.next;
        }

        return intersectCount;
    }

    @Override
    public int contains( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback )
    {
        final CellSpan span = getCellSpan( offset, radius, true, new CellSpan() );

        int containCount = 0;

        span.reset();
        
        do
        {
            containCount = contains( offset, radius, max, collidesWith, callback, containCount, cells[span.index] );

            if ( containCount == max )
            {
                break;
            }
        } 
        while (span.next());
        
        if ( containCount < max && isOutsidePartially( offset, radius ) )
        {
            containCount = contains( offset, radius, max, collidesWith, callback, containCount, outside );
        }

        return containCount;
    }

    private int contains( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback, int containCount, LinkedList<SpatialGridNode<V>> list )
    {
        final LinkedNode<SpatialGridNode<V>> end = list.head;
        LinkedNode<SpatialGridNode<V>> start = end.next;

        while ( start != end )
        {
            final LinkedNode<SpatialGridNode<V>> next = start.next;
            final SpatialEntity<V> a = start.value.entity;

            if ( !a.isInert() && (a.getSpatialGroups() & collidesWith) != 0 )
            {
                final float aradius2 = a.getRadius() * 2;
                final float overlap = SpatialUtility.overlap( a, offset, radius );

                if ( overlap >= aradius2 )
                {
                    if ( callback.onFound( a, radius - overlap, containCount, offset, radius, max, collidesWith ) )
                    {
                        containCount++;

                        if ( containCount >= max )
                        {
                            break;
                        }
                    }
                }
            }

            start = next;
        }

        return containCount;
    }

    @Override
    public int knn( V offset, int k, long collidesWith, SpatialEntity<V>[] nearest, float[] distance )
    {
        if ( !SpatialUtility.prepareKnn( k, nearest, distance ) )
        {
            return 0;
        }

        int near = knn( offset, k, collidesWith, nearest, distance, 0, entities );

        if ( isOutsidePartially( offset, 0.0f ) )
        {
            near = knn( offset, k, collidesWith, nearest, distance, near, outside );
        }

        return near;
    }

    private int knn( V offset, int k, long collidesWith, SpatialEntity<V>[] nearest, float[] distance, int near, LinkedList<SpatialGridNode<V>> list )
    {
        final LinkedNode<SpatialGridNode<V>> end = list.head;
        LinkedNode<SpatialGridNode<V>> start = end.next;

        while ( start != end )
        {
            final LinkedNode<SpatialGridNode<V>> next = start.next;
            final SpatialEntity<V> a = start.value.entity;

            if ( !a.isInert() && (a.getSpatialGroups() & collidesWith) != 0 )
            {
                near = SpatialUtility.accumulateKnn( SpatialUtility.distance( a, offset ), a, near, k, distance, nearest );
            }

            start = next;
        }

        return near;
    }

    private V getCellIndex( V vector )
    {
        return vector.subi( offset ).muli( sizeInversed ).floori();
    }

    private V getCellIndexClamped( V vector )
    {
        getCellIndex( vector );

        for ( int i = 0; i < vector.size(); i++ )
        {
            float c = vector.getComponent( i );
            float dim = dimensions.getComponent( i );

            if ( c < 0 )
            {
                vector.setComponent( i, 0 );
            }
            else if ( c > dim )
            {
                vector.setComponent( i, dim - 1 );
            }
        }

        return vector;
    }

    private CellSpan getCellSpan( V center, float radius, boolean lookback, CellSpan out )
    {
        return getCellSpan( center.add( -radius ), center.add( +radius ), lookback, out );
    }

    private CellSpan getCellSpan( V min, V max, boolean lookback, CellSpan out )
    {
        out.min = getCellIndexClamped( min );
        out.max = getCellIndexClamped( max );

        if ( lookback )
        {
            V back = min.create();
            int startIndex = getIndexFromCellIndex( min );

            for ( int i = 0; i < back.size(); i++ )
            {
                int currentIndex = startIndex;
                int currentDelta = (int)dimensionsScale.getComponent( i );
                int minIndex = (int)min.getComponent( i );
                int maxIndex = (int)max.getComponent( i );

                for ( int k = minIndex; k <= maxIndex; k++ )
                {
                    back.maxi( back, cells[currentIndex].lookback );
                    currentIndex += currentDelta;
                }
            }

            min.subi( back );
            min.maxi( min, min.create() );
        }

        return out;
    }

    public boolean isOutsidePartially( V center, float radius )
    {
        return !center.isBetween( min, max, radius );
    }

    public class CellSpan
    {

        public V min, max;

        public int index;
        public V current;

        public void reset()
        {
            current = min.clone();
            index = getIndexFromCellIndex( current );
        }

        public boolean next()
        {   
            final int n = current.size();
            boolean overflow = true;
                        
            for ( int i = 0; i <= n && overflow; i++ )
            {
                float next = current.getComponent( i ) + 1;
                overflow = next > max.getComponent( i );
                
                if ( overflow )
                {
                    if ( i == n )
                    {
                        return false;
                    }
                    
                    next = min.getComponent( i );
                }

                current.setComponent( i, next );
            }
            
            index = getIndexFromCellIndex( current );
            
            return (index != -1);
        }

    }

}
