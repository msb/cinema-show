# Mineforge Mod - Cinema Show

This project does 3 things:

- Processes sets of images and outputs the resources block textures for a set of minecraft blocks
  that can be placed in game to build a "screen" that animates the given images.
- [Generates the resources](https://docs.minecraftforge.net/en/latest/datagen/) for these minecraft blocks.
- Builds a Mineforge Mod that includes and registers these resources.

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

Each folder defines a separate film for which a different set of blocks will be generated. (Each
block set will have a different tab with a name matching the folder?).

For folder "Show 1":

- Each image is re-sized on the X axis according to `blocksx` i.e. `5 * 16 = 80 px` and on the 
  Y axis such that the aspect ratio is preserved.
- The minimum image Y length (rounded down to the nearest factor of 16) is calculated.
- Each image is cropped centrally to this length.
- The images are cut into separate block textures and "collated" as animated block textures.
  For instance, the 3 squares from the bottom left of each image are re-constituted as a 
  `16 x 48 px` image named `{top folder slug}-{sub folder slug}-{x index}-{y index}.png` (maybe).
- With each new image a `{top folder slug}-{sub folder slug}-{x index}-{y index}.mcmeta` file is
  created that defines the animation frame rate as `frametime` (defined in ticks).
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

## Assumptions/Limitations/Notes

- It would be nice to define a single "screen" block which renders different block of different
  screens depending on state within the game (TDB) because:
  - You wouldn't have to a rebuild the mod everytime you wanted to add another screen.
  - It would be much easier to construct a screen in game.
- Because only one 'cinema-show' jar can be applied to an MC install I think the image name can be
  simplified.
- This looks like a good example for
  [Simple Java Image Scaling and Cropping](https://medium.com/@SatyaRaj_PC/simple-java-image-scaling-and-cropping-33f95e7d9278).
- [The Cinema Show (showing my age)](https://www.youtube.com/watch?v=G501Ii0X0NE).
