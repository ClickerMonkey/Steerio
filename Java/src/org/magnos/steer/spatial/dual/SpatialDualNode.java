
package org.magnos.steer.spatial.dual;

import org.magnos.steer.spatial.CollisionCallback;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.SpatialUtility;
import org.magnos.steer.spatial.base.LinkedListBounds;
import org.magnos.steer.util.LinkedNode;
import org.magnos.steer.vec.Vec;


public class SpatialDualNode<V extends Vec<V>> extends LinkedListBounds<V, SpatialEntity<V>>
{

    public static final int CHILD_COUNT = 4;

    public int size;
    public int overflowResizes;
    public int underflowResizes;
    public SpatialDualNode<V> minNode;
    public SpatialDualNode<V> maxNode;
    public final SpatialDualNode<V> parent;
    public final int axis;

    public SpatialDualNode( SpatialDualNode<V> parent, int axis, V min, V max )
    {
        super( min, max );

        this.parent = parent;
        this.axis = axis;
    }

    private void internalAdd( LinkedNode<SpatialEntity<V>> node )
    {
        add( node );
        size++;
    }

    private void internalRemove( LinkedNode<SpatialEntity<V>> node )
    {
        node.remove();
        size--;
    }

    public void add( SpatialEntity<V> entity )
    {
        if ( isLeaf() )
        {
            internalAdd( new LinkedNode<SpatialEntity<V>>( entity ) );
        }
        else
        {
            if ( minNode.isContained( entity.getPosition(), entity.getRadius() ) )
            {
                minNode.add( entity );
            }
            else if ( maxNode.isContained( entity.getPosition(), entity.getRadius() ) )
            {
                maxNode.add( entity );
            }
            else
            {
                internalAdd( new LinkedNode<SpatialEntity<V>>( entity ) );
            }
        }
    }

    public int refresh()
    {
        int alive = 0;

        LinkedNode<SpatialEntity<V>> start = head.next;

        while ( start != head )
        {
            final LinkedNode<SpatialEntity<V>> next = start.next;
            final SpatialEntity<V> entity = start.value;

            if ( entity.isInert() )
            {
                internalRemove( start );
            }
            else
            {
                SpatialDualNode<V> node = getNode( entity );

                if ( node != this )
                {
                    internalRemove( start );
                    node.internalAdd( start );
                }
                else
                {
                    alive++;
                }
            }

            start = next;
        }

        if ( isBranch() )
        {
            alive += minNode.refresh();
            alive += maxNode.refresh();
        }

        return alive;
    }

    public SpatialDualNode<V> getNode( SpatialEntity<V> entity )
    {
        SpatialDualNode<V> node = this;

        while ( node.parent != null && !node.isContained( entity.getPosition(), entity.getRadius() ) )
        {
            node = node.parent;
        }

        if ( node != this )
        {
            return node;
        }

        boolean placing = true;

        while ( placing )
        {
            if ( node.isBranch() )
            {
                if ( node.minNode.isContained( entity.getPosition(), entity.getRadius() ) )
                {
                    node = node.minNode;
                }
                else if ( node.maxNode.isContained( entity.getPosition(), entity.getRadius() ) )
                {
                    node = node.maxNode;
                }
                else
                {
                    placing = false;
                }
            }
            else
            {
                placing = false;
            }
        }

        return node;
    }

    public void resize( int desiredSize, int resizeThreshold )
    {
        // Leaves can expand if their size >= desiredSize for resizeThreshold number of resize requests.
        if ( isLeaf() )
        {
            if ( size >= desiredSize )
            {
                overflowResizes++;
            }
            else
            {
                overflowResizes = 0;
            }

            if ( overflowResizes >= resizeThreshold )
            {
                overflowResizes = 0;
                expand();
            }
        }
        // Branches can shrink if their total size < desiredSize for resizeThreshold number of resize requests AND all their children are leaves.
        else
        {
            // Try to resize children
            minNode.resize( desiredSize, resizeThreshold );
            maxNode.resize( desiredSize, resizeThreshold );

            if ( minNode.isLeaf() && maxNode.isLeaf() )
            {
                if ( getSize() < desiredSize )
                {
                    underflowResizes++;
                }
                else
                {
                    underflowResizes = 0;
                }

                if ( underflowResizes >= resizeThreshold )
                {
                    underflowResizes = 0;
                    shrink();
                }
            }
            else
            {
                underflowResizes = 0;
            }
        }
    }

    public void expand()
    {
        int childAxis = (axis + 1) % min.size();
        
        V minNodeMin = min.clone();
        V minNodeMax = max.clone();
        V maxNodeMin = min.clone();
        V maxNodeMax = max.clone();
        
        minNodeMax.setComponent( axis, center.getComponent( axis ) );
        maxNodeMin.setComponent( axis, center.getComponent( axis ) );
        
        minNode = new SpatialDualNode<V>( this, childAxis, minNodeMin, minNodeMax );
        maxNode = new SpatialDualNode<V>( this, childAxis, maxNodeMin, maxNodeMax );
        
        LinkedNode<SpatialEntity<V>> start = head.next;

        while ( start != head )
        {
            final LinkedNode<SpatialEntity<V>> next = start.next;
            final SpatialEntity<V> entity = start.value;

            if ( minNode.isContained( entity.getPosition(), entity.getRadius() ) )
            {
                internalRemove( start );
                minNode.internalAdd( start );
            }
            else if ( maxNode.isContained( entity.getPosition(), entity.getRadius() ) )
            {
                internalRemove( start );
                maxNode.internalAdd( start );
            }

            start = next;
        }
    }

    public void shrink()
    {
        final LinkedNode<SpatialEntity<V>> endtl = minNode.head;
        LinkedNode<SpatialEntity<V>> starttl = endtl.next;

        while ( starttl != endtl )
        {
            final LinkedNode<SpatialEntity<V>> next = starttl.next;
            minNode.internalRemove( starttl );
            internalAdd( starttl );
            starttl = next;
        }

        final LinkedNode<SpatialEntity<V>> endbr = maxNode.head;
        LinkedNode<SpatialEntity<V>> startbr = endbr.next;

        while ( startbr != endbr )
        {
            final LinkedNode<SpatialEntity<V>> next = startbr.next;
            maxNode.internalRemove( startbr );
            internalAdd( startbr );
            startbr = next;
        }

        minNode = maxNode = null;
    }

    public void destroy()
    {
        if ( isBranch() )
        {
            minNode.destroy();
            maxNode.destroy();
            minNode = maxNode = null;
        }

        LinkedNode<SpatialEntity<V>> start = head.next;

        while ( start != head )
        {
            LinkedNode<SpatialEntity<V>> next = start.next;
            start.remove();
            start = next;
        }

        size = 0;
    }

    public int handleCollisions( CollisionCallback<V> callback )
    {
        int collisionCount = 0;

        // Handle collisions within this node
        LinkedNode<SpatialEntity<V>> start = head.next;
        while ( start != head )
        {
            final SpatialEntity<V> a = start.value;

            if ( !a.isInert() )
            {
                collisionCount += handleCollisionsAgainstList( a, start, head, callback );
            }

            start = start.next;
        }

        // Handle collisions with children and within children
        if ( isBranch() )
        {
            // Handle collisions between the entities in this node and the entities in the children
            start = head.next;
            while ( start != head )
            {
                collisionCount += handleCollisionsWithChildren( start.value, callback );
                start = start.next;
            }

            // Handle collisions for children
            collisionCount += minNode.handleCollisions( callback );
            collisionCount += maxNode.handleCollisions( callback );
        }

        return collisionCount;
    }

    private int handleCollisionsWithChildren( SpatialEntity<V> a, CollisionCallback<V> callback )
    {
        return minNode.handleEntityCollisionFromParent( a, callback ) +
               maxNode.handleEntityCollisionFromParent( a, callback );
    }

    private int handleEntityCollisionFromParent( SpatialEntity<V> a, CollisionCallback<V> callback )
    {
        int collisionCount = 0;

        if ( !a.isInert() && isIntersecting( a.getPosition(), a.getRadius() ) )
        {
            collisionCount += handleCollisionsAgainstList( a, head, head, callback );

            if ( isBranch() )
            {
                collisionCount += handleCollisionsWithChildren( a, callback );
            }
        }

        return collisionCount;
    }

    private int handleCollisionsAgainstList( SpatialEntity<V> a, LinkedNode<SpatialEntity<V>> start, LinkedNode<SpatialEntity<V>> end, CollisionCallback<V> callback )
    {
        start = start.next;

        int collisionCount = 0;

        while ( start != end )
        {
            final LinkedNode<SpatialEntity<V>> next = start.next;
            final SpatialEntity<V> b = start.value;

            if ( !b.isInert() )
            {
                collisionCount = SpatialUtility.handleCollision( a, start.value, collisionCount, callback );

                if ( a.isInert() )
                {
                    break;
                }
            }

            start = next;
        }

        return collisionCount;
    }

    public int intersects( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback, int intersectCount )
    {
        LinkedNode<SpatialEntity<V>> start = head.next;

        while ( start != head )
        {
            final SpatialEntity<V> a = start.value;

            if ( !a.isInert() && (collidesWith & a.getSpatialGroups()) != 0 )
            {
                final float overlap = SpatialUtility.overlap( a, offset, radius );

                if ( overlap > 0 && callback.onFound( a, overlap, intersectCount, offset, radius, max, collidesWith ) )
                {
                    intersectCount++;

                    if ( intersectCount >= max )
                    {
                        break;
                    }
                }
            }
            start = start.next;
        }

        if ( intersectCount < max && isBranch() )
        {
            if ( minNode.isIntersecting( offset, radius ) )
            {
                intersectCount = minNode.intersects( offset, radius, max, collidesWith, callback, intersectCount );
            }
            if ( intersectCount < max && maxNode.isIntersecting( offset, radius ) )
            {
                intersectCount = maxNode.intersects( offset, radius, max, collidesWith, callback, intersectCount );
            }
        }

        return intersectCount;
    }

    public int contains( V offset, float radius, int max, long collidesWith, SearchCallback<V> callback, int containCount )
    {
        LinkedNode<SpatialEntity<V>> start = head.next;

        while ( start != head )
        {
            final SpatialEntity<V> a = start.value;

            if ( !a.isInert() && (collidesWith & a.getSpatialGroups()) != 0 )
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
            start = start.next;
        }

        if ( containCount < max && isBranch() )
        {
            if ( minNode.isIntersecting( offset, radius ) )
            {
                containCount = minNode.contains( offset, radius, max, collidesWith, callback, containCount );
            }
            if ( containCount < max && maxNode.isIntersecting( offset, radius ) )
            {
                containCount = maxNode.contains( offset, radius, max, collidesWith, callback, containCount );
            }
        }

        return containCount;
    }

    public int knn( V offset, int k, long collidesWith, SpatialEntity<V>[] nearest, float[] distance, int near )
    {
        LinkedNode<SpatialEntity<V>> start = head.next;

        while ( start != head )
        {
            final SpatialEntity<V> a = start.value;

            if ( !a.isInert() && (a.getSpatialGroups() & collidesWith) != 0 )
            {
                near = SpatialUtility.accumulateKnn( SpatialUtility.distance( a, offset ), a, near, k, distance, nearest );
            }

            start = start.next;
        }

        if ( isBranch() )
        {
            near = minNode.knn( offset, k, collidesWith, nearest, distance, near );
            near = maxNode.knn( offset, k, collidesWith, nearest, distance, near );
        }

        return near;
    }

    public boolean isLeaf()
    {
        return (minNode == null);
    }

    public boolean isBranch()
    {
        return (minNode != null);
    }

    public int getSize()
    {
        return size + getChildrenSize();
    }

    public int getChildrenSize()
    {
        return (minNode != null ? minNode.getSize() + maxNode.getSize() : 0);
    }

}
