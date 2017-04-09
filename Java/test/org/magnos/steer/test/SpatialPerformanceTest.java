package org.magnos.steer.test;

import org.magnos.steer.SteerMath;
import org.magnos.steer.SteerSubject;
import org.magnos.steer.spatial.SearchCallback;
import org.magnos.steer.spatial.SpatialDatabase;
import org.magnos.steer.spatial.SpatialEntity;
import org.magnos.steer.spatial.dual.SpatialDualTree;
import org.magnos.steer.spatial.grid.SpatialGrid;
import org.magnos.steer.spatial.quad.SpatialQuadTree;
import org.magnos.steer.vec.Vec2;


public class SpatialPerformanceTest
{

    public static final float SIZE = 4000;
    public static final float QUERY = 25;
    
    public static void main(String[] args)
    {
        run( 50000, 1000, "Grid", new SpatialGrid<Vec2>( new Vec2(SIZE / 100, SIZE / 100), new Vec2(0, 0), new Vec2(100, 100) ) );
        run( 50000, 1000, "Quad", new SpatialQuadTree<Vec2>( new Vec2(0, 0),  new Vec2(SIZE, SIZE), 100, 10 ) );
        run( 50000, 1000, "Dual", new SpatialDualTree<Vec2>( new Vec2(0, 0),  new Vec2(SIZE, SIZE), 100, 10 ) );

        run( 250000, 1000, "Grid", new SpatialGrid<Vec2>( new Vec2(SIZE / 100, SIZE / 100), new Vec2(0, 0), new Vec2(100, 100) ) );
        run( 250000, 1000, "Quad", new SpatialQuadTree<Vec2>( new Vec2(0, 0),  new Vec2(SIZE, SIZE), 100, 10 ) );
        run( 250000, 1000, "Dual", new SpatialDualTree<Vec2>( new Vec2(0, 0),  new Vec2(SIZE, SIZE), 100, 10 ) );

        run( 500000, 1000, "Grid", new SpatialGrid<Vec2>( new Vec2(SIZE / 100, SIZE / 100), new Vec2(0, 0), new Vec2(100, 100) ) );
        run( 500000, 1000, "Quad", new SpatialQuadTree<Vec2>( new Vec2(0, 0),  new Vec2(SIZE, SIZE), 100, 10 ) );
        run( 500000, 1000, "Dual", new SpatialDualTree<Vec2>( new Vec2(0, 0),  new Vec2(SIZE, SIZE), 100, 10 ) );
    }
    
    private static void run(int points, int iterations, String name, SpatialDatabase<Vec2> db)
    {
        System.out.format("%s: running %d iterations with %d points\n", name, iterations, points );
        
        long startBuild = System.nanoTime();
        
        for (int i = 0; i < points; i++)
        {
            db.add( new Ent( SteerMath.randomFloat( SIZE ), SteerMath.randomFloat( SIZE ) ) );
        }
        
        long endBuild = System.nanoTime();
        double buildTime = (endBuild - startBuild) * 0.000000001;
        
        System.out.format( "%s populated in %.9f seconds.\n", name, buildTime );
        
        Vec2 queryPoint = new Vec2( SteerMath.randomFloat( SIZE ), SteerMath.randomFloat( SIZE ) );
        
        SearchCallback<Vec2> callback = (entity, overlap, index, queryOffset, queryRadius, queryMax, queryGroups) -> (true); 
        
        double total = 0;
        double count = 0;
        int intersects = 0;
        
        for (int i = 0; i < iterations; i++)
        {
            long start = System.nanoTime();
            intersects = db.intersects( queryPoint, QUERY, 1000, 1, callback );
            long end = System.nanoTime();
            
            if (i > iterations - 10)
            {
                double seconds = (end - start) * 0.000000001;
                
                // System.out.format( "%d found in %.9f seconds.\n", intersects, seconds );
                
                total += seconds;
                count++;
            }
        }
        
        System.out.format( "%s average query time of %.9f seconds (%d found).\n", name, total / count, intersects ); 
    }
    
    private static class Ent implements SpatialEntity<Vec2>
    {
        public Vec2 position;
        
        public Ent(float x, float y)
        {
            this.position = new Vec2( x, y );
        }
        
        @Override
        public SpatialEntity<Vec2> getTarget( SteerSubject<Vec2> subject )
        {
            return this;
        }

        @Override
        public Vec2 getPosition()
        {
            return position;
        }

        @Override
        public Vec2 getPosition( Vec2 out )
        {
            return out.set( position );
        }

        @Override
        public float getRadius()
        {
            return 0;
        }

        @Override
        public float getDistanceAndNormal( Vec2 origin, Vec2 lookahead, Vec2 outNormal )
        {
            return 0;
        }

        @Override
        public long getSpatialGroups()
        {
            return 1;
        }

        @Override
        public long getSpatialCollisionGroups()
        {
            return 0;
        }

        @Override
        public boolean isStatic()
        {
            return true;
        }

        @Override
        public boolean isInert()
        {
            return false;
        }

        @Override
        public void attach( Object attachment )
        {
            
        }

        @Override
        public <T> T attachment()
        {
            return null;
        }

        @Override
        public <T> T attachment( Class<T> type )
        {
            return null;
        }

        @Override
        public Vec2 getDirection()
        {
            return null;
        }

        @Override
        public Vec2 getVelocity()
        {
            return null;
        }

        @Override
        public float getMaximumVelocity()
        {
            return 0;
        }

        @Override
        public Vec2 getAcceleration()
        {
            return null;
        }

        @Override
        public float getMaximumAcceleration()
        {
            return 0;
        }
        
    }
    
}
