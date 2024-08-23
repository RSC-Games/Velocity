# Velocity
An open-source, extensible game engine written in Java. It supports various rendering backends via the Velocity Extensible Renderer Architecture.

# Renderers
Velocity comes bundled with VXRA, a rendering architecture suitable for connecting multiple different renderers to the same code. By default, Velocity ships with the Embedded Render Pipeline (ERP), a stripped down render pipeline with minimal features. A couple of other renderers are also endorsed by RSC Games and supplied with a Velocity setup. These renderers are:
  - LumaViper Rendering Engine:
      A feature-rich render pipeline with support for multiple rendering backends.
    - LVCPU:
      The CPU backend of LumaViper. The reference implementation for most VXRA-based renderers. Current testing suggests this renderer will easily handle 1080p with 70 light sources at > 60 FPS on an i7-12700k.
    - LVOGL:
      A performance-oriented hardware accelerated rendering engine. Based on OpenGL 3.3 Core, this is the recommended renderer for use.
    - LVDX11:
      Originally planned for Intel GPUs that are too old to support OGL3.3 but have native DX10 support. Cancelled since the JNI library I was using doesn't support most DX11 functions.

The Embedded Render Pipeline is a rather weak pipeline that really only exists so if better renderers aren't available or can't be linked in to the project, then your game can still run.

# Debug Renderer
Velocity initially was written with a debug renderer. This allowed one to see whether the rendering code was working and free-look across the entire scene. However, since VXRA was mainlined into Velocity ~7 months ago, the Debug Renderer has remained broken and will eventually be removed from Velocity's source completely. Multi-camera rendering on the native render pipeline will replace it.

# Sprite System
Initially, Velocity was loosely based on Unity's component system. I quickly saw this wouldn't work. At all. Unity uses a lot of memory. And it can. One of Velocity's main design focuses was low memory usage. Instantiating ~20 components every time a sprite was created would have eaten through memory pretty quickly. Instead, I went for specialization. There are a large amount of specialized classes in Velocity. For example, Renderables are designed to be drawn on screen. UIRenderables are drawable too, except on screen. There's a Collidable class as well. Feel free to browse Velocity's source for more.

I felt this system gives the most in terms of flexibility. Instead of attaching a bunch of specialized components to a class, instead just create one that is specialized for your purposes.
