# Minecraft Forge Mod - The Cinema Show

This project does 3 things:

- Processes sets of images and outputs the resources block textures for a set of minecraft blocks
  that can be placed in-game to build a "screen" that animates the given images.
- [Generates the resources](https://docs.minecraftforge.net/en/latest/datagen/) for these minecraft
  blocks.
- Builds a Mineforge Mod that includes and registers these resources.

## The Game Mechanics

A defined show can be constructed from a single screen block. The screen block can be searched for
in the inventory by it's show name. When this block is placed it will render as the bottom left
tile of the show with the screen facing towards you. Then as you place blocks above (relatively)
and to the right of the previous blocks they render the correct tile for the show (facing in the
same direction as the other block). If you place a screen block above/right outside the bounds of
the show it will render as a new bottom left tile. If you place a screen block anywhere else it
also will render as a new bottom left tile.

To get a better idea of the mechanics have a look at
[an in-game demonstration of the block](https://youtu.be/v1jXgT7rQ5g).

## How the image processing works

As an illustration, consider the following set of images.

- cinema-show-source (folder - ignored by `git`)
  - 1st-show (folder)
    - meta.json
      - showName = "Falling down"
      - blocksX = 5
      - frameTime = 25
    - Image 0.jpg
    - Image 1.png
    - Image 2.jpg
  - something-else (folder)
    - meta.json
      - showName = "Getting up"
      - blocksY = 3
      - frameTime = 10
    - Image A.png
    - Image B.png
    - Image C.jpg
    - Image D.png

Each folder defines a separate show (a set of image files) for which a different set of resources
will be generated. Along with the set of image files there is `meta.json` that defines various
properties. For instance, the `showName` parameter defines the show name.

For folder "1st-show":

- Each image is re-sized on the X axis according to `blocksX` i.e. `5 * 16 = 80 px` and on the 
  Y axis such that the aspect ratio is preserved.
- The minimum image Y length (rounded down to the nearest factor of 16) is calculated.
- Each image is cropped centrally to this length.
- The images are cut into separate block textures and "collated" as animated block textures.
  For instance, the 3 squares from the bottom left of each image are re-constituted as a 
  `16 x 48 px` image named `{showName slug}-{x index}-{y index}.png`
- With each new image a `{showName slug}-{x index}-{y index}.mcmeta` file is created that defines
  the animation frame rate as `frameTime` (defined in ticks).
- The images are outputted to the `generatedTextures` resource folder in the 
  `assets.cinemashow.textures.block` package.
- The `meta.json` is written to `assets.cinemashow` as `{showName slug}.json` to be available to
  the code during resource generation (see above) and in-game. Additionally `blocksY` is written to
  this file.

Note:

- For "Show 2" `blocksY` is specified for so the same re-size/crop method is applied but for a
  different axis.
- The lexicographic order of the image files will give the play order. The image file name is not
  used for anything else.
- If `frameTime` isn't given,
  [a default is assumed](https://github.com/msb/cinema-show/blob/main/src/main/java/uk/me/msb/cinemashow/ShowProperties.java#L36).
- If neither `blocksX` or `blocksY` is given,
  [`blocksX` is set to the maximum value](https://github.com/msb/cinema-show/blob/main/src/main/java/uk/me/msb/cinemashow/ShowProperties.java#L26).
- If `showName` isn't given, it is given the name of the show folder.
- At the end of the generation process a `shows.json` file will be written to `assets.cinemashow`
  listing the slugs for all of the shows. This will be used subsequently to find show resources.

## Running

- A gradle task `runGenerateTextures` is defined to run the process described above.
- The [data generation task (`runData`)](https://docs.minecraftforge.net/en/latest/datagen/)
  generates the resources for these textures.
- The normal `build` task can be used to generate the final `jar`.

## Assumptions/Limitations/Notes

- When testing with server you need to:
  - update `run/eula.txt`
  - in `run/server.properties` set `online-mode=false`
- [The Cinema Show (showing my age)](https://www.youtube.com/watch?v=G501Ii0X0NE).
