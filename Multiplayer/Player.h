#pragma once
#include <SDL.h>
#include <SDL_image.h>
#include "Game.h"
#include <string>

class Player {
public:
	Player(int id, float x = 0, float y = 0, SDL_Renderer* renderer  = nullptr);

	void setPos(float setX, float setY);
	void getPos(float& getX, float& getY);

	void handleInput(const std::string& input);
	void update(float deltaTime);
	void render(SDL_Renderer* renderer);

	float getX() const { return x; }
	float getY() const { return y; }
	float getRot() const { return rot; }

private:
	int playerId;

	float x, y;
	float xVel, yVel;
	float rot;

	bool movingForward = false, movingBackward = false;
	bool turningLeft = false, turningRight = false;

	float acceleration = 0.5f;
	float rotationSpeed = 360.0f;
	float drag = 0.9f;
	float maxSpeed = 5.0f;

	SDL_Rect srcRect = { 0,0,0,0 };
	SDL_Rect destRect = { 0,0,0,0 };
	SDL_Texture* spriteTexture = nullptr;
};