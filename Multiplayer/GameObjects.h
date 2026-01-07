#pragma once
#include <SDL.h>
#include <SDL_image.h>
#include "Game.h"
#include <string>

class GameObject {
public:
	GameObject();
	GameObject(const char* textureName , float x = 0, float y = 0, SDL_Renderer* renderer = nullptr);

	void setPos(float setX, float setY, float setRot);
	void getPos(float& getX, float& getY, float& getRot);
	void setSize(int pWidth, int pHeight);
	virtual void render(SDL_Renderer* renderer) {
		SDL_RenderCopyEx(renderer, spriteTexture, &srcRect, &destRect, rot, nullptr, SDL_FLIP_NONE);
	};

	float getX() const { return x; }
	float getY() const { return y; }
	float getRot() const { return rot; }
protected:
	float x, y;
	float xVel, yVel;
	float rot;
	float acceleration;
	float rotationSpeed;
	float drag = 0.9f;
	float maxSpeed;
	SDL_Rect srcRect = { 0,0,0,0 };
	SDL_Rect destRect = { 0,0,0,0 };
	SDL_Texture* spriteTexture = nullptr;
};
class Player : public GameObject {
public:
	Player(const char* textureName,int id, float x = 0, float y = 0, SDL_Renderer* renderer = nullptr);
	void handleInput(const std::string& input);
	void rotateBarrel();
	void render(SDL_Renderer* renderer);
	void setMousePos(int mouseXPos, int mouseYPos);
	void setBarRot(float newBarRot);
	void setAlive(float alive);
	bool getAlive() { return isAlive; }
	SDL_Point barPivot = {
		32 / 2,
		32 - 15
	};
	float barRot;
	bool isAlive = true;
private:
	int playerId;

	bool movingForward = false, movingBackward = false, turningLeft = false, turningRight = false;

	float acceleration = 0.5f;
	float rotationSpeed = 360.0f;
	float drag = 0.9f;
	float maxSpeed = 5.0f;
	float mouseX = 0, mouseY = 0 ;


	SDL_Rect barSrcRect = { 0,0,0,0 };
	SDL_Texture* barTexture = nullptr;



};
class Bullet : public GameObject {
public:
	Bullet(float x = 0, float y = 0, SDL_Renderer* renderer = nullptr);

private:

};
class Tile : public GameObject {
public:
	Tile(const char* textureName, float x = 0, float y = 0, SDL_Renderer* renderer = nullptr);
	enum Type { EMPTY, SOLID, BREAKABLE, HOLE, PSPAWN };
	Type type;
};
