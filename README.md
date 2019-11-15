# Pure functional HTTP APIs in Scala #

[![Build Status](https://travis-ci.org/jan0sch/pfhais.svg?branch=master)](https://travis-ci.org/jan0sch/pfhais)

This repository contains the source code for the book "Pure functional 
HTTP APIs in Scala" which is available on Leanpub: https://leanpub.com/pfhais

## Structure ##

The folder `manuscript` contains the book source code and other resources 
like images. The service implementations are placed in the folders 
`impure` and `pure`. Within the folder `tapir` you can find the pure 
implementation which uses the [tapir library](https://github.com/softwaremill/tapir) 
for a typed API design.

### Build Tooling ###

All project modules can be compiled, run and tested via [sbt](https://www.scala-sbt.org/).
Just open a terminal in the desired folder (e.g. `pure`) and start the `sbt` shell.

## Copyleft Notice ##

This book uses the Creative Commons Attribution ShareAlike 4.0 International 
(CC BY-SA 4.0) license. The code snippets in this book are licensed under 
CC0 which means you can use them without restriction. 
Excerpts from libraries maintain their license.

## Code Coverage ##

[![codecov](https://codecov.io/gh/jan0sch/pfhais/branch/master/graphs/sunburst.svg)](https://codecov.io/gh/jan0sch/pfhais)
