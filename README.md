# Velocity
An open-source, extensible game engine written in Java. It supports various rendering backends via the Velocity Extensible Renderer Architecture.

# Renderers
Velocity comes bundled with VXRA, a rendering architecture suitable for connecting multiple different renderers to the same code. By default, Velocity ships with the Embedded Render Pipeline (ERP), a stripped down render pipeline with minimal features. A couple of other renderers are also endorsed by RSC Games and supplied with a Velocity setup. These renderers are:
  - LumaViper Rendering Engine:
      A feature-rich render pipeline with support for multiple rendering backends.
    - LVCPU:
      The CPU backend of LumaViper. The reference implementation for most VXRA-based renderers.
    - LVOGL:
      A performance-oriented hardware accelerated rendering engine.