#version 330 core

layout (location = 0) in vec3 in_Pos;
layout (location = 1) in vec4 in_Color;
layout (location = 2) in vec2 in_TexCoord;
layout (location = 3) in float in_TexID;

out vec4 color;
out vec2 texCoord;
out float texID;

void main()
{
    color = in_Color;
    texCoord = in_TexCoord;
    texID = in_TexID;
    gl_Position = vec4(in_Pos, 1.0);
}