#include <iostream>
#include "SDL.h"
#include "SDL_net.h"
#include "Game.h"


const char* IP_NAME = "localhost";
const Uint16 PORT = 55555;



Game* game = nullptr;

bool is_running = false;
static int on_receive(void* socket_ptr) {
	TCPsocket socket = (TCPsocket)socket_ptr;

	const int message_length = 1024;

	char message[message_length];
	int received;

	while(is_running) {
		received = SDLNet_TCP_Recv(socket, message, message_length);

		if (received <= 0) {
			is_running = false;
			break;
		}

		message[received] = '\0';
		char* context = nullptr;
		char* pch = strtok_s(message, ",", &context);

		// get the command, which is the first string in the message
		std::string cmd(pch);

		// then get the arguments to the command
		std::vector<std::string> args;

		while (pch != NULL) {
			pch = strtok_s(NULL, ",", &context);

			if (pch != NULL) {
				args.push_back(std::string(pch));
			}
		}

		game->on_receive(cmd, args);

		if (cmd == "exit") {
			break;
		}

	}

	return 0;
}
static int on_send(void* socket_ptr) {
	TCPsocket socket = (TCPsocket)socket_ptr;

	while (is_running) {
		if (!game->messages.empty()) {
			std::string message = "CLIENT_DATA";
			for (auto& m : game->messages)
				message += "," + m;

			game->messages.clear();

			message += "\n"; // critical for Java readLine()
			SDLNet_TCP_Send(socket, message.c_str(), message.length());

			std::cout << "[Client] Sent: " << message;
		}
		SDL_Delay(1);
	}

	return 0;
}

int main(int argc, char** argv) {
	SDL_Init(SDL_INIT_EVERYTHING);
	SDLNet_Init();
	const int frameDelay = 1000 / FPS;
	Uint64 frameStart;
	int deltaTime = 20;

	game = new Game;

	is_running = game->isRunning();

	IPaddress ip;
	if (SDLNet_ResolveHost(&ip, IP_NAME, PORT) == -1) {
		printf("SDLNet_ResolveHost: %s\n", SDLNet_GetError());
		exit(3);
	}
	TCPsocket socket = SDLNet_TCP_Open(&ip);
	
	if (!socket) {
		printf("SDLNet_TCP_Open: %s\n", SDLNet_GetError());
		exit(4);
	}
	SDL_Thread* recvThread = SDL_CreateThread(on_receive, "ConnectionReceiveThread", (void*)socket);
	SDL_Thread* sendThread = SDL_CreateThread(on_send, "ConnectionSendThread", (void*)socket);


	// Handle Game Behaviour


	while (game->isRunning()) {
		frameStart = SDL_GetTicks64();
		
		game->update();

		SDL_Event e;
		while (SDL_PollEvent(&e)) {
			if (e.type == SDL_QUIT) {
				game->Close();
				is_running = false;
				SDLNet_TCP_Close(socket);

			}
				
			game->input(e);
		}
		//Limit FPS
		deltaTime = SDL_GetTicks64() - frameStart;
		if (frameDelay > deltaTime) SDL_Delay(frameDelay - deltaTime);
	}

	SDL_WaitThread(recvThread, nullptr);
	SDL_WaitThread(sendThread, nullptr);
	//Delete Game
	

	SDLNet_Quit();

	SDL_Quit();
	

	return 0;
}



