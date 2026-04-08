#version 330 core

in vec4 color;

uniform sampler2D u_Textures[32];

void main()
{
    gl_FragColor = color;
}
