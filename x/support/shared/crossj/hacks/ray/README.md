Just some code working through the book "The Ray Tracer Challenge" by Jamis Buck

=====

Ugh, yuck, looking ahead at the book, I'm realizing that this book mostly just
sticks with the Phong model and doesn't really do a globally illuminated model
(i.e. "real" ray tracing).

====

From merging ray3:

Instead of following the books/articles,
for this one, I'm treating those references more like guidelines.


References:
* https://raytracing.github.io/books/RayTracingInOneWeekend.html
    (this was my primary reference for ray2 until I abandoned it)

TODO: Merge ray3 and ray

===

Sample cli usage:

./run-class-java crossj.hacks.ray3.Main -v \
    data/ray/mod/scene1/vp.mtl \
    data/ray/mod/scene1/cow1.obj \
    data/ray/mod/scene1/walls1.obj \
    -c 0,0,8/0,0,0
