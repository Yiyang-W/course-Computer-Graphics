

In this project, I accomplished three line renderers, one polygon renderer, and some test cases.

The line renderers are DDA line renderer, Bresenham line renderer and antialiasing line renderer. DDA line renderer and Bresenham line renderer are done simply following their algorithm. Antialiasing line renderer is implemented as follows:
	1. For every column, find the pixel should be set after rounding coordinate y.
	2. Assuming a pixel is a circle with radius 0.5 and the width of the line is 1, calculate the area the line has inside a pixel respectively for the found pixel, the one above that and the one below that.
	3. For these pixels, divide the area by the area of a pixel, get the percentage and use it for function setPixelWithCoverage().

The polygon renderer is implemented as follows:
	1. Get the left chain and the right chain of the polygon, respectively containing the points of left side and right side in order from top to down(including).
	2. Set the color of the polygon roughly by getting the average r, g, b of all points.
	3. Get two points l1, l2 on the left and two r1, r2 on the right. l1 and r1 are the same for they are both the top point initially.
	4. Get the maximum y from l2 and r2, set a point q on the other side, we can get a triangle or a trapezoid.
	5. Draw it from top to bottom, from left to right. Draw the top and the left, do not do the bottom and the right.
	6. Below the trapezoid we can get another two points. Repeat it from step 4 until done.

Test cases are written as required.


There are some flaws. For the Antialiasing line renderer, the brightness of lines differs, and for all line renderers, the color of a line is decided only by one point. It can cause some trouble, for example, if we want two lines with different color from one point. For the polygon renderer, there are for brighter points in the circle on the ghost panel, and I don't know what to do. And the color of a polygon is the average r, g, b of all points with the top point and the bottom point counted twice for easy calculation. Also, the polygon renderer doesn't do antialiasing.


On the page 6, I want to show that the polygon renderer can draw polygons indeed other than triangles.