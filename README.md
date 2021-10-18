# Pure functional HTTP APIs in Scala #

[![Build Status](https://app.travis-ci.com/jan0sch/pfhais.svg?branch=main)](https://app.travis-ci.com/jan0sch/pfhais)
[![codecov](https://codecov.io/gh/jan0sch/pfhais/branch/main/graph/badge.svg?token=t6jfwoRMKJ)](https://codecov.io/gh/jan0sch/pfhais)

This repository contains the source code for the book "Pure functional 
HTTP APIs in Scala" which is available on Leanpub: https://leanpub.com/pfhais

While Leanpub is the recommended source the book is also available at:

- [Amazon](https://www.amazon.de/dp/B092JJGLVW)
- [Thalia](https://www.thalia.de/shop/home/artikeldetails/ID151277747.html)
- and others

An online course based upon the book was done by Educative: https://www.educative.io/courses/pure-functional-http-apis-scala

## Structure ##

The folder `manuscript` contains the book source code and other resources 
like images. The service implementations are placed in the folders 
`impure` and `pure`. Within the folder `tapir` you can find the pure 
implementation which uses the [tapir library](https://github.com/softwaremill/tapir) 
for a typed API design.

### Build Tooling ###

All project modules can be compiled, run and tested via [sbt](https://www.scala-sbt.org/).
Just open a terminal in the desired folder (e.g. `pure`) and start the `sbt` shell.

If you are using the IntelliJ IDEA development environment then you will 
need to install the Scala plugin for it. Afterwards you should be able to 
create IDEA projects by using the "Import Project" feature and point it to 
the desired folder (e.g. `tapir`).

## Copyleft Notice ##

This book uses the Creative Commons Attribution ShareAlike 4.0 International 
(CC BY-SA 4.0) license. The code snippets in this book are licensed under 
CC0 which means you can use them without restriction. 
Excerpts from libraries maintain their license.

