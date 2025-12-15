#pragma once
#include <SDL.h>
#include <SDL_image.h>
#include "Game.h"
#include <string>

class GameObject {
public:
	GameObject(float x = 0, float y = 0, SDL_Renderer* renderer = nullptr);

	void setPos(float setX, float setY, float setRot);
	void getPos(float& getX, float getY, float getRot);

	void render(SDL_Renderer* renderer);

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
	Player(int id, float x = 0, float y = 0, SDL_Renderer* renderer = nullptr);
	void handleInput(const std::string& input);
private:
	int playerId;

	bool movingForward = false, movingBackward = false, turningLeft = false, turningRight = false;

	float acceleration = 0.5f;
	float rotationSpeed = 360.0f;
	float drag = 0.9f;
	float maxSpeed = 5.0f;
};
class Bullet : public GameObject {
public:
	Bullet(float x = 0, float y = 0, SDL_Renderer* renderer = nullptr);

private:

};