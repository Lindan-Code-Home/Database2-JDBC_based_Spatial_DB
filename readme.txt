Name: Lindan Tian
USC ID: 3086874846
Email:lindanti@usc.edu
1 run the created.sql in sql developer.
2 compile the HW2.java in cmd using javac �classpath  .;* HW2.java (windows 7).
3 run in cmd using java �classpath .;* HW2 arguments
For example for the window query example in the HW2 Assignment I run the command:
 java �classpath .;* HW2 firebuilding 10 20 300 500
To run the demo, for example
java �classpath .;* HW2 demo 1
4 run the dropdb.sql to drop all the tables created above.

The map illustrates all the builidings' location in the DBMS.

(1) If query type = window: we perform a window query in this case. That is, finding all objects that are completely inside the query window.
    $java hw2 window firebuilding 10 20 300 500
    lists the ID’s of all buildings on fire that are completely inside the query window with lower left vertex (10, 20) and upper right vertex (300, 500).

(2)If query type = within: we do a within-distance query in this case. That is, finding all objects that are within the given distance to the given object(s).
   $java hw2 within firehydrant OHE 250
   lists the ID’s of all firehydrants that are within distance 250 to all buildings named OHE.

(3)If query type = nn: we do a nearest neighbor query in this case.
   $java hw2 nn building b3 5
   lists the ID’s of the 5 nearest buildings to b3.
   
(4)If query type = demo: in this case we print the results of the following hard-coded demos on the screen.
   The demos are as follows:
   1. Find the names of the buildings with name initial ’S’ that are NOT on fire.
   2. For each building on fire, list its name and the ID’s of the 5 nearest firehydrants.
   3. We say a firehydrant covers a building if it is within distance 120 to that building. Find the ID’s of the firehydrants that cover the most buildings.
   4. We say a firehydrant is called a reverse nearest neighbor of a building if it is that building’s nearest firehydrant. Find the ID’s of the top 5 firehydrants that have the most reverse nearest neighbors together with their number of reverse nearest neighbors.
   5. Find the coordinates of the lower left and upper right vertex of the MBR that fully contains all buildings whose names are of the form ’%HE’. Note that you cannot manually figure out these buildings in your program.
