steerio
=======

Steerio is a steering behavior and spatial database library for games.

**What's a steering behavior you ask?**  
A steering behavior is something that helps an object in your game move around in an intelligent and realistic way.

**What are spatial databases?**  
A spatial database is a structure that you can add geometry to (like circles) and quickly query to find all colliding geometries, intersecting, contained, and K nearest neighbors.

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

**Performance**  
***QUAD TREE***  
Refresh Statistics  
  Total Iterations: 100  
	Average: 0.000198237  
	Min: 0.000188351  
	Max: 0.000236043  
Collision Statistics  
	Total Iterations: 100  
	Average: 0.000616374  
	Min: 0.000590107  
	Max: 0.000739823  
  
***DUAL TREE*** 
Refresh Statistics  
	Total Iterations: 100  
	Average: 0.000189507  
	Min: 0.000177183  
	Max: 0.000221253  
Collision Statistics  
	Total Iterations: 100  
	Average: 0.000636301  
	Min: 0.000607010  
	Max: 0.000730767  
  
***SWEEP AND PRUNE***   
Refresh Statistics  
	Total Iterations: 100  
	Average: 0.001039657  
	Min: 0.000827961  
	Max: 0.001866308  
Collision Statistics  
	Total Iterations: 100  
	Average: 0.002738293  
	Min: 0.002565079  
	Max: 0.005157022  
  
***GRID***  
Refresh Statistics  
	Total Iterations: 100  
	Average: 0.000423223  
	Min: 0.000407793  
	Max: 0.000469973  
Collision Statistics  
	Total Iterations: 100  
	Average: 0.000676963  
	Min: 0.000627838  
	Max: 0.000769102  
  
***BRUTE FORCE***  
Refresh Statistics  
	Total Iterations: 100  
	Average: 0.000029937  
	Min: 0.000025355  
	Max: 0.000036826  
Collision Statistics  
	Total Iterations: 100  
	Average: 0.538688420  
	Min: 0.531306765  
	Max: 0.548827362  
