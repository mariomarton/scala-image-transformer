

# scala-image-transformer

The app loads images (jpg/png/bmp), translates them into ASCII art, optionally applies filters (rotation, scaling, inversion), and saves and/or prints them.


## Running the app

**Allowed arguments for 'run' in sbt:**
  
* `--image PATH`
  : _Specify the path to the image file._

* `--image-random`
  : _Generate a random image as the source._

* `--rotate NUMBER`
  : _Apply rotation by the specified degrees._

* `--scale NUMBER`
  : _Apply scaling by the specified factor._

* `--invert`
  : _Apply an inversion filter to the image._

* `--output-console`
  : _Export the output to the console. (Default, if no output arg passed.)_

* `--output-file PATH`
  : _Export the output to a file at the given path._

* `--table TABLE_NAME`
  : _Specify a grayscale-to-ASCII conversion table._

* `--custom-table CHARACTERS`
  : _Specify a custom grayscale-to-ASCII table using characters._


Allowed TABLE_NAME values are: `linear` and `non-linear`.
