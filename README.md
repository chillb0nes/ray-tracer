# Java Ray Tracer

[![Build Status](https://travis-ci.com/Stingray42/ray-tracer.svg?branch=master)](https://travis-ci.com/Stingray42/ray-tracer)
[![codecov](https://codecov.io/gh/Stingray42/ray-tracer/branch/master/graph/badge.svg)](https://codecov.io/gh/Stingray42/ray-tracer)
[![Known Vulnerabilities](https://snyk.io/test/github/Stingray42/ray-tracer/badge.svg)](https://snyk.io/test/github/Stingray42/ray-tracer)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Stingray42/ray-tracer.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Stingray42/ray-tracer/context:java)
[![Total alerts](https://img.shields.io/lgtm/alerts/g/Stingray42/ray-tracer.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Stingray42/ray-tracer/alerts/)

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
Use GUI to add or remove objects, import sample scene, or press `Ctrl(⌘)+Alt+Z` to generate *random* scene.

## Screenshots
![](https://raw.githubusercontent.com/Stingray42/ray-tracer/master/src/main/resources/screenshots/0.png)
![](https://raw.githubusercontent.com/Stingray42/ray-tracer/master/src/main/resources/screenshots/1.png)
![](https://raw.githubusercontent.com/Stingray42/ray-tracer/master/src/main/resources/screenshots/2.png)
![](https://raw.githubusercontent.com/Stingray42/ray-tracer/master/src/main/resources/screenshots/3.png)

## Credits

Ray-tracing algorithms mostly based on [scratchapixel.com](https://www.scratchapixel.com/) tutorials

Icons by [icons8.com](https://icons8.com/)

Suzanne by [blender.org](https://www.blender.org/)
