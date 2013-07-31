![](http://i1150.photobucket.com/albums/o604/ClickerMonkey/Steerio1_zpsebbab7bb.png)
=======

![Development](http://i4.photobucket.com/albums/y123/Freaklotr4/stage_development.png)

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

|                                 | Average (s)               |
|:------------------------------- |:------------------------- |
| SpatialQuadTree                 | 0.000198237 ± 0.000047692 |
| SpatialDualTree                 | 0.000189507 ± 0.000044070 |
| SpatialSweepAndPrune            | 0.001039657 ± 0.001038347 |
| SpatialGrid                     | 0.000423223 ± 0.000062180 |
| SpatialArray (brute force)      | 0.000029937 ± 0.000011471 |

**Collision Statistics** (the amount of time it takes to determine all collided entities)  

|                                 | Average (s)               |
|:------------------------------- |:------------------------- |
| SpatialQuadTree                 | 0.000616374 ± 0.000149716 |
| SpatialDualTree                 | 0.000636301 ± 0.000123757 |
| SpatialSweepAndPrune            | 0.002738293 ± 0.002591943 |
| SpatialGrid                     | 0.000676963 ± 0.000141264 |
| SpatialArray (brute force)      | 0.538688420 ± 0.017520597 |

**TODO**  
Things I need to explain: Targets, Steering Behavior, SteerSet, SteerModifier, SpatialDatabase queries, SpatialDatabase geometries, Path implementations  
Each Steering Behavior implementation  
Each Target implementation  
Decision Tree for which Spatial Database to use  
Pros/Cons for Spatial Databases  
Each SpatialDatabase implementation  
Pictures for all of it!  
