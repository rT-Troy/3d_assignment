## COM3503: 3D Computer Graphics: Assignment

### Declaration
 I declare that this code is my own work 
 
 Author Jun Zhang jzhang213@sheffield.ac.uk 

### Texture Source
snow_background.jpg - 
 <a href="https://www.freepik.com/free-vector/watercolor-winter-landscape_33137531.htm#query=snow%20anime&position=3&from_view=keyword&track=ais&uuid=9c133cc0-f68a-49c3-88cb-e6fb4c592d30#position=3&query=snow%20anime">Freepik</a>

snowfall.jpg - 
<a href="https://www.freepik.com/free-vector/christmas-snowy-background_3236043.htm#query=snow%20effect&from_query=snoweffect&position=3&from_view=search&track=sph&uuid=1fb19e8f-1582-43b3-851f-cb61266b3f04">Image by kjpargeter</a> on Freepik

Quicksand.jpg - [https://www.pexels.com/license/]

jade.jpg - [http://libnoise.sourceforge.net/examples/textures/index.html]

ear0xuu2.jpg - [https://github.com/nasa/NASA-3D-Resources]

jup0vss1.jpg - [https://github.com/nasa/NASA-3D-Resources]

### Code Source
Thanks to Dr. Steve Maddock for providing the tutorial and material. Some of the code in this project is derived from his work.

### My Main Works
For the convenience of evaluation, the files with major changes are listed below for your reference, other files may also have modifications.

#### Aliens.java: 
add button, could switch every light, rock or roll every alien independently

#### Aliens_GLEventListener.java: 
create 2 alien, 1 lampPost objects from their class, 
aliens are in different textures.
build and joint floor and background in different shader,
the background rely on mixed texture.

#### Alien.java: 
alien class

#### Light.java: 
set different type light by boolean argument, could switch independently

#### LampPost.java:
lamp post class

#### Model.java: 
multiple lights support

#### vs_background.txt: 
keep the snowfall moving

#### fs_background.txt: 
mix two textures 
