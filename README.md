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
  - The dimensions of the screens are limited to 10x10 blocks (do we need to limit this?).

## How the image processing works

As an illustration, consider the following set of images.

- Cinema Show (folder - maybe defined as an additional resource folder?)
  - Show 1 (folder)
    - meta.json
      - blocksx = 5
      - frametime = 25
    - Image 0.jpg
    - Image 1.png
    - Image 2.jpg
  - Show 2 (folder)
    - meta.json
      - blocksy = 3
      - frametime = 10
    - Image A.png
    - Image B.png
    - Image C.jpg
    - Image D.png

Each folder defines a separate film for which a different set of resources will be generated.
The shows will be assigned to screen slots in lexicographic order. So "Show 1" will be assigned
to `screen-alpha` and "Show 2" will be assigned to `screen-bravo`. 

For folder "Show 1":

- Each image is re-sized on the X axis according to `blocksx` i.e. `5 * 16 = 80 px` and on the 
  Y axis such that the aspect ratio is preserved.
- The minimum image Y length (rounded down to the nearest factor of 16) is calculated.
- Each image is cropped centrally to this length.
- The images are cut into separate block textures and "collated" as animated block textures.
  For instance, the 3 squares from the bottom left of each image are re-constituted as a 
  `16 x 48 px` image named `{block name}-{x index}-{y index}.png`.
- With each new image a `{block name}-{x index}-{y index}.mcmeta` file is created that defines the
  animation frame rate as `frametime` (defined in ticks).
- The images are outputted to the `generated` resource folder in the 
  `assets.cinemashow.texture.block` package.

Note:

- For "Show 2" `blocksy` is specified for so the same re-size/crop method is applied but for a
  different axis.
- The lexicographic order of the image files will give the play order. The image file name is not
  used for anything else.

## Running

- A gradle task `generateTextures` is defined to run the process described above.
- The [data generation task (`runData`)](https://docs.minecraftforge.net/en/latest/datagen/)
  generates the resources for these textures.
- The normal `build` task can be used to generate the final `jar`.
- How to we create seperate mod and datapack jars.

## Assumptions/Limitations/Notes

- This looks like a good example for
  [Simple Java Image Scaling and Cropping](https://medium.com/@SatyaRaj_PC/simple-java-image-scaling-and-cropping-33f95e7d9278).
- It should be possible to tag screen blocks with their show names (eg, "Show 1") to improve the
  block search.
- Could have a separate tab for the screen blocks.
- Might be preferable to assign shows to screens using `meta.json`.
- Need to think placing up/down facing screens (each can face in four directions).
- [The Cinema Show (showing my age)](https://www.youtube.com/watch?v=G501Ii0X0NE).

### Show Block Names

- `screen-alpha`
- `screen-bravo`
- `screen-charlie`
- `screen-delta`
- `screen-echo`
- `screen-foxtrot`
- `screen-golf`
- `screen-hotel`
- `screen-india`
- `screen-juliet`
- `screen-kilo`
- `screen-lima`
- `screen-mike`
- `screen-november`
- `screen-oscar`
- `screen-papa`
- `screen-quebec`
- `screen-romeo`
- `screen-sierra`
- `screen-tango`
- `screen-uniform`
- `screen-victor`
- `screen-whiskey`
- `screen-xray`
- `screen-yankee`
- `screen-zulu`

