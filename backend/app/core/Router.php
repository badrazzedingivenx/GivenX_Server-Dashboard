<?php
/*
Rôle du fichier :
Ce fichier gère le routage de l’API.

Ce qu’il contient :
- une classe Router
- une liste de routes
- des méthodes pour enregistrer et exécuter les routes
- la gestion des routes dynamiques avec paramètres comme /api/servers/{id}

Pourquoi il existe :
Il permet d’associer une URL et une méthode HTTP à une action PHP.

Comment il s’intègre dans le projet :
Le fichier public/index.php utilise ce routeur pour diriger les requêtes vers les bons contrôleurs.
*/

class Router
{
    private array $routes = [];

    public function get(string $path, callable|array $handler): void
    {
        $this->addRoute('GET', $path, $handler);
    }

    public function post(string $path, callable|array $handler): void
    {
        $this->addRoute('POST', $path, $handler);
    }

    public function put(string $path, callable|array $handler): void
    {
        $this->addRoute('PUT', $path, $handler);
    }

    public function delete(string $path, callable|array $handler): void
    {
        $this->addRoute('DELETE', $path, $handler);
    }

    private function addRoute(string $method, string $path, callable|array $handler): void
    {
        $this->routes[] = [
            'method' => $method,
            'path' => $path,
            'handler' => $handler
        ];
    }

    private function matchRoute(string $routePath, string $requestUri): array|false
    {
        $paramNames = [];

        $pattern = preg_replace_callback('/\{([a-zA-Z_][a-zA-Z0-9_]*)\}/', function ($matches) use (&$paramNames) {
            $paramNames[] = $matches[1];
            return '([^\/]+)';
        }, $routePath);

        $pattern = '#^' . $pattern . '$#';

        if (!preg_match($pattern, $requestUri, $matches)) {
            return false;
        }

        array_shift($matches);

        $params = [];
        foreach ($paramNames as $index => $name) {
            $params[$name] = $matches[$index] ?? null;
        }

        return $params;
    }

    public function dispatch(string $method, string $uri): void
    {
        foreach ($this->routes as $route) {
            if ($route['method'] !== $method) {
                continue;
            }

            $params = $this->matchRoute($route['path'], $uri);

            if ($params === false) {
                continue;
            }

            $handler = $route['handler'];

            if (is_callable($handler)) {
                call_user_func($handler, $params);
                return;
            }

            if (is_array($handler) && count($handler) === 2) {
                [$controllerClass, $controllerMethod] = $handler;

                if (!class_exists($controllerClass)) {
                    Response::json([
                        'success' => false,
                        'message' => "Contrôleur introuvable : {$controllerClass}"
                    ], 500);
                }

                $controller = new $controllerClass();

                if (!method_exists($controller, $controllerMethod)) {
                    Response::json([
                        'success' => false,
                        'message' => "Méthode introuvable : {$controllerMethod}"
                    ], 500);
                }

                $controller->$controllerMethod($params);
                return;
            }
        }

        Response::json([
            'success' => false,
            'message' => 'Route non trouvée',
            'requested_method' => $method,
            'requested_uri' => $uri
        ], 404);
    }
}
