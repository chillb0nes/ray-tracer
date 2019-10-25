# Java Ray Tracer

This is simple ray tracer with JavaFX GUI that can render some 3D figures. 

## Features

- Multi-threaded ray tracing
- Cache for rendered images
- Sphere, Triangle and Mesh(group of triangles) rendering
- GUI with many different controls for scene settings
- Import/Export scene in JSON or YAML formats
- Gluon Ignite and Spring for DI

## Installation
```cmd
git clone git@github.com:Stingray42/ray-tracer.git
cd ray-tracer
mvn package
```

## Usage
```cmd
java -jar target/ray-tracer-1.0.0.jar
```
Use GUI to add or remove objects, import sample scene, or press `Ctrl+Alt+Z` to generate *random* scene.

## Screenshots
![](https://raw.githubusercontent.com/Stingray42/ray-tracer/master/src/main/resources/screenshots/0.png)
![](https://raw.githubusercontent.com/Stingray42/ray-tracer/master/src/main/resources/screenshots/1.png)
![](https://raw.githubusercontent.com/Stingray42/ray-tracer/master/src/main/resources/screenshots/2.png)

## Credits

Ray-tracing algorithms mostly based on [scratchapixel.com](https://www.scratchapixel.com/) tutorials

Icons by [icons8.com](https://icons8.com/)

Suzanne by [blender.org](https://www.blender.org/)