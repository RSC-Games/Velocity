#version 330 core

in vec4 color;
in vec2 texCoord;
in float texID;

uniform sampler2D u_Textures[32];

void main()
{
    vec4 chosenTex;
    chosenTex = texture(u_Textures[int(texID)], texCoord);
    gl_FragColor = chosenTex * color;   
}
