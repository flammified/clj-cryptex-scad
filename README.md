# Cryptex using scad-clj

This repository contains the Clojure code that generates a cryptex lock using scad-clj. It outputs it as an OpenSCAD file, which can then be used to create other 3D model files, such as STL files.

Running the main.clj file will output the cryptex under the output/ folder. The function `cryptex` takes an argument for the word that is the solution. Change this and all other sizes will resize as well.

This is an example output:

`<insert image>`
  
It consists of a main body, a stick (the part with the pins), the rings (which need to be put on the main body in the right order!) and a stop-end, to put on the end of the main body.
  
