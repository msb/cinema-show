# Mineforge Mod - The Cinema Show

This project does 3 things:

- Processes sets of images and outputs the resources block textures for a set of minecraft blocks
  that can be placed in-game to build a "screen" that animates the given images.
- [Generates the resources](https://docs.minecraftforge.net/en/latest/datagen/) for these minecraft
  blocks.
- Builds a Mineforge Mod that includes and registers these resources.

## The Game Mechanics

26 new "screen" blocks are registered by the mod. These can be thought of as slots to assign your
animation to. They are named using the phonetic alphabet (see below). In-game, if a screen has
been assigned a show and that screen block is placed it will render as the bottom left tile of the
show with the screen facing towards you. Then as you place blocks above (relatively) and to the
right of the previous blocks they render the correct tile for the show (facing in the same direction
as the other block). If you place a screen block above/right outside bounds of the animation it will
render as a warning block. If you place a screen block anywhere else it will render as a new bottom
left tile.

I had originally considered using different blocks for the different screen tiles. However, I think
this approach has some advantages. 

- Advantages of the single block per screen approach
  - Constructing a screen in-game is much simpler.
  - A full mod rebuild isn't required for every new show - data packs with new show can be
  - dropped in.
- Disadvantages of the single block per screen approach
  - The number of screens is limited.
  - MC doesn't seem optimised for handling custom state large number of possible values so the 
    dimensions of the screens are limited to 9x9 blocks.

## How the image processing works

As an illustration, consider the following set of images.

- cinema-show-source (folder - ignored by `git`)
  - 1st-show (folder)
    - meta.json
      - showName = "Falling down"
      - blocksX = 5
      - frameTime = 25
      - assignToBlock = "screen_alpha"
    - Image 0.jpg
    - Image 1.png
    - Image 2.jpg
  - something-else (folder)
    - meta.json
      - showName = "Getting up"
      - blocksY = 3
      - frameTime = 10
      - assignToBlock = "screen_zulu"
    - Image A.png
    - Image B.png
    - Image C.jpg
    - Image D.png

Each folder defines a separate show (a set of image files) for which a different set of resources
will be generated. Along with the set of image files there is `meta.json` that defines various
properties. For instance, the `assignToBlock` parameter defines which screen block the show will be
assigned to.

For folder "1st-show":

- Each image is re-sized on the X axis according to `blocksX` i.e. `5 * 16 = 80 px` and on the 
  Y axis such that the aspect ratio is preserved.
- The minimum image Y length (rounded down to the nearest factor of 16) is calculated.
- Each image is cropped centrally to this length.
- The images are cut into separate block textures and "collated" as animated block textures.
  For instance, the 3 squares from the bottom left of each image are re-constituted as a 
  `16 x 48 px` image named `{block name}-{x index}-{y index}.png`.
- With each new image a `{block name}-{x index}-{y index}.mcmeta` file is created that defines the
  animation frame rate as `frameTime` (defined in ticks).
- The images are outputted to the `generatedTextures` resource folder in the 
  `assets.cinemashow.textures.block` package.
- The `meta.json` is written to `assets.cinemashow` as `{block name}.json` to be available to the
  code during resource generation (see above) and in-game. Additionally `blocksY` is written to
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

## Running

- A gradle task `runGenerateTextures` is defined to run the process described above.
- The [data generation task (`runData`)](https://docs.minecraftforge.net/en/latest/datagen/)
  generates the resources for these textures.
- The normal `build` task can be used to generate the final `jar`.
- TODO: How do we create separate mod and datapack jars?

## Assumptions/Limitations/Notes

- [An in-game example](https://youtu.be/OOTtlrH0opE). TODO replace this with a better one.
- Build: For simplicity of deployment I'm including the classes from the additional `cinemashow`
  dependencies directly in the MOD jar. This feel a bit hacky
- Gradle config: It isn't clear to me how to generate separate data packs
- MC isn't optimised for dealing with state `Property` objects with a large number of possible values (ScreenStateProperty).
- When testing with server you need to:
  - update `run/eula.txt`
  - in `run/server.properties` set `online-mode=false`
- [The Cinema Show (showing my age)](https://www.youtube.com/watch?v=G501Ii0X0NE).

### Screen Block Names

- `screen_alpha`
- `screen_bravo`
- `screen_charlie`
- `screen_delta`
- `screen_echo`
- `screen_foxtrot`
- `screen_golf`
- `screen_hotel`
- `screen_india`
- `screen_juliet`
- `screen_kilo`
- `screen_lima`
- `screen_mike`
- `screen_november`
- `screen_oscar`
- `screen_papa`
- `screen_quebec`
- `screen_romeo`
- `screen_sierra`
- `screen_tango`
- `screen_uniform`
- `screen_victor`
- `screen_whiskey`
- `screen_xray`
- `screen_yankee`
- `screen_zulu`

