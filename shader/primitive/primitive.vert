#version 330 core

layout (location = 0) in vec3 in_Pos;
layout (location = 1) in vec4 in_Color;

out vec4 color;

void main()
{
    color = in_Color;
    gl_Position = vec4(in_Pos, 1.0);
}