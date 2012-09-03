# Ocean WebGL demo

![](https://raw.github.com/pkamenarsky/ocean/master/screenshot/ocean.jpg)

This is a small WebGL demo showcasing procedural waves with fresnel reflections, thus simulating a realistic water surface. It runs in all modern WebGL compatible browsers. The demo is written in Java and compiled to JavaScript using the [Google Web Toolkit](https://developers.google.com/web-toolkit/) compiler.

You can rotate the view by pressing either `A` or `D`. 

## Live Demo

Check out a complete functioning live demo [here](http://pkamenarsky.github.com/ocean/ocean.html).

*Note:* Firefox seems to have problems loading the textures when the page is opened by clicking the link with the middle mouse button. When that happens, either just reload the page or just copy the link and paste it into a fresh tab.

## Building

First download and istall [GWT](https://developers.google.com/web-toolkit/). Then open `build.xml` and adjust the following line:

    <property name="gwt.sdk" location="../gwt"/>

to your specific GWT path. Now run:

    ant gwtc-GL

Open `release\ocean.html` and enjoy :)
