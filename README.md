cms-modern-theme
================

Original Enonic CMS comes with hard to read small fonts and enough old 256 color images.

![Enonic CMS admin area screenshot](http://www.fotohost.by/pic_b/14/01/13/57cfaf837eef8044cd03ea08328de7b3.png)


This plugin changes resources and CSS'es on the fly. just put jar to CMS_HOME/plugins directory and it will work. minimum CMS version requirement is Enonic CMS 4.7.4 .


![Enonic CMS admin area screenshot with theme](http://www.fotohost.by/pic_b/14/01/13/52ee4458769e5a9427f0e0578d2f9a7c.png)

#### Structure of theme

/resources folder contains images to override. the path to image inside this folder must be the same as in url.

/scripts folder contains javascripts to edit CSS dymamically. the name must be name of resource + .js

#### Building

1. clone repository
2. mvn package

