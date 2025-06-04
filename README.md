# BackEnd_SBA301

http://localhost:8080/swagger-ui.html

PS D:\FPT\Ky_7\SBA301_BE_git\BE_SBA301> docker-compose up -d redis
[+] Running 1/1
✔ Container redis-local Started 0.4s
PS D:\FPT\Ky_7\SBA301_BE_git\BE_SBA301> docker ps
CONTAINER ID IMAGE COMMAND CREATED STATUS PORTS NAMES
8eb092b2126a redis:latest "docker-entrypoint.s…" 6 seconds ago Up 5 seconds 0.0.0.0:6379->6379/tcp redis-local
PS D:\FPT\Ky_7\SBA301_BE_git\BE_SBA301> docker exec -it redis-local redis-cli ping
PONG
