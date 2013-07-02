
![](http://i1150.photobucket.com/albums/o604/ClickerMonkey/Steerio_zps8c0d940a.png)
=======

Steerio is a steering behavior, spatial database, and path library for games.

**What's a steering behavior you ask?**  
A steering behavior is something that helps an object in your game move around in an intelligent and realistic way.

**What are spatial databases?**  
A spatial database is a structure that you can add geometry to (like circles) and quickly query to find all colliding geometries, intersecting, contained, and K nearest neighbors.

**What do you mean by Path?**  
A path can be a chain of line segments or a curve going through a defined set of points.

**What languages is this available in?**  
Java is the main language, but it has been ported to Javascript, C#, C++, and C.

**I want to see some cool examples!**
- Mini RTS
- Geometry Wars Clone
- Capture the Flag Simulation
- Asteroids Simluation

**What type of games use steering behaviors?**
- RTS = unit movement, horde formations
- FPS = unit movement, AI that can dodge/maneuver around its environment
- Sports = 

#### Spatial Database Performance

The following performance scenarios involved 10,000 objects (~5% stationary, ~95% bouncing around the world boundaries) with the size between 1 and 4 in a space the size of 12,800 by 12,800 running at 100fps for 1000 frames.

**Refresh Statistics** (a refresh is done once per frame after all entity positions have been updated)  

|                                 | Average (s) | Min (s)     | Max (s)     |
|:------------------------------- | -----------:| -----------:| -----------:|
| SpatialQuadTree                 | 0.000198237 | 0.000188351 | 0.000236043 |
| SpatialDualTree                 | 0.000189507 | 0.000177183 | 0.000221253 |
| SpatialSweepAndPrune            | 0.001039657 | 0.000827961 | 0.001866308 |
| SpatialGrid                     | 0.000423223 | 0.000407793 | 0.000469973 |
| SpatialArray (brute force)      | 0.000029937 | 0.000025355 | 0.000036826 |

**Collision Statistics** (the amount of time it takes to determine all collided entities)  

|                                 | Average (s) | Min (s)     | Max (s)     |
|:------------------------------- | -----------:| -----------:| -----------:|
| SpatialQuadTree                 | 0.000616374 | 0.000590107 | 0.000739823 |
| SpatialDualTree                 | 0.000636301 | 0.000607010 | 0.000730767 |
| SpatialSweepAndPrune            | 0.002738293 | 0.002565079 | 0.005157022 |
| SpatialGrid                     | 0.000676963 | 0.000627838 | 0.000769102 |
| SpatialArray (brute force)      | 0.538688420 | 0.531306765 | 0.548827362 |

**TODO**  
Things I need to explain: Targets, Steering Behavior, SteerSet, SteerModifier, SpatialDatabase queries, SpatialDatabase geometries, Path implementations  
Each Steering Behavior implementation  
Each Target implementation  
Decision Tree for which Spatial Database to use  
Pros/Cons for Spatial Databases  
Each SpatialDatabase implementation  
Pictures for all of it!  
