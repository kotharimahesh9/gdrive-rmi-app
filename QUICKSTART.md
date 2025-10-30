# GDrive RMI Application - Quick Start Guide

## Your Application is Now Running!

All services have been successfully containerized and are running.

### Current Status

```
✓ Part 1 Server - Running on localhost:1099 (Healthy)
✓ Part 1 Client - Running and ready
✓ Part 2 Server - Running on localhost:1100 (Healthy)
✓ Part 2 Client - Running and ready
✓ Part 3 Server - Running on localhost:1101 (Healthy)
✓ Part 3 Client - Running and ready
```

## Step-by-Step Usage Instructions

### 1. Check All Services Are Running

```bash
docker-compose ps
```

You should see all 6 containers with "Up" and "(healthy)" status.

### 2. Interact with Part 1 Client (File Operations)

**Attach to the client:**
```bash
docker attach gdrive-part1-client
```

**You'll see a menu like this:**
```
----- FILE CLIENT MENU -----
1. Upload File
2. Delete File
3. Rename File
4. Exit
Enter your choice:
```

**To detach without stopping:**
Press `Ctrl+P`, then immediately press `Ctrl+Q`

### 3. Interact with Part 2 Client (Auto-Sync)

**View logs (automatic synchronization):**
```bash
docker logs -f gdrive-part2-client
```

**Add a file to sync:**
```bash
# Create a test file in the sync directory
docker exec gdrive-part2-client bash -c "echo 'Test content' > /app/Part2/client_sync/test.txt"

# Check if it synced
docker logs gdrive-part2-client --tail 20
```

### 4. Interact with Part 3 Client (Computations)

**Attach to the client:**
```bash
docker attach gdrive-part3-client
```

**You'll see a menu like this:**
```
----- COMPUTATION CLIENT MENU -----
1. Synchronous Addition
2. Asynchronous Addition
3. Synchronous Array Sorting
4. Asynchronous Array Sorting
5. Exit
Enter your choice:
```

### 5. View Server Logs

**Part 1 Server:**
```bash
docker logs -f gdrive-part1-server
```

**Part 2 Server:**
```bash
docker logs -f gdrive-part2-server
```

**Part 3 Server:**
```bash
docker logs -f gdrive-part3-server
```

Press `Ctrl+C` to stop following logs.

### 6. Stop All Services

```bash
docker-compose down
```

### 7. Restart Services

```bash
docker-compose up -d
```

### 8. View All Logs at Once

```bash
docker-compose logs -f
```

## Testing Each Part

### Test Part 1: File Upload

1. Attach to Part 1 client:
   ```bash
   docker attach gdrive-part1-client
   ```

2. Select option `1` (Upload File)

3. Enter filename from the clientFiles directory:
   ```
   helloWorld.txt
   ```

4. Check server logs to see the upload:
   ```bash
   docker logs gdrive-part1-server
   ```

### Test Part 2: Auto-Sync

1. Add a new file to the sync directory:
   ```bash
   docker exec gdrive-part2-client bash -c "echo 'Hello Sync' > /app/Part2/client_sync/hello.txt"
   ```

2. Watch the client logs:
   ```bash
   docker logs gdrive-part2-client --tail 30
   ```

3. Verify on server:
   ```bash
   docker exec gdrive-part2-server ls -la /app/Part2/server_uploads/
   ```

### Test Part 3: Computation

1. Attach to Part 3 client:
   ```bash
   docker attach gdrive-part3-client
   ```

2. Select option `1` (Synchronous Addition)

3. Enter two numbers when prompted

4. View the result

5. Detach: Press `Ctrl+P` then `Ctrl+Q`

## Advanced Commands

### Access Container Shell

```bash
# Access Part 1 server
docker exec -it gdrive-part1-server /bin/bash

# Access Part 1 client
docker exec -it gdrive-part1-client /bin/bash
```

### Copy Files to/from Containers

```bash
# Copy file TO container
docker cp myfile.txt gdrive-part1-client:/app/Part1/clientFiles/

# Copy file FROM container
docker cp gdrive-part1-server:/app/Part1/server_uploads/myfile.txt ./
```

### View Specific Service Logs

```bash
# Last 50 lines
docker-compose logs --tail=50 part1-server

# Follow specific service
docker-compose logs -f part1-client

# All logs without following
docker-compose logs
```

### Restart a Single Service

```bash
# Restart Part 1 server only
docker-compose restart part1-server

# Restart Part 1 client only
docker-compose restart part1-client
```

### Run Only Specific Parts

**Part 1 only:**
```bash
docker-compose up -d part1-server part1-client
```

**Part 2 only:**
```bash
docker-compose up -d part2-server part2-client
```

**Part 3 only:**
```bash
docker-compose up -d part3-server part3-client
```

## Troubleshooting

### Client Not Responding

```bash
# Check if container is running
docker ps

# Restart the client
docker-compose restart part1-client

# Attach again
docker attach gdrive-part1-client
```

### Server Connection Issues

```bash
# Check server health
docker-compose ps

# View server logs
docker logs gdrive-part1-server

# Restart server and client
docker-compose restart part1-server part1-client
```

### Complete Reset

```bash
# Stop and remove everything (including volumes)
docker-compose down -v

# Rebuild and start
docker-compose up -d --build
```

## Port Information

- **Part 1 Server**: localhost:1099
- **Part 2 Server**: localhost:1100
- **Part 3 Server**: localhost:1101

## Data Persistence

Your data is stored in Docker volumes:

```bash
# List volumes
docker volume ls | grep gdrive

# Inspect a volume
docker volume inspect gdrive_base_impl_part1-uploads
```

## Stopping Everything

```bash
# Stop containers (data persists)
docker-compose down

# Stop and remove volumes (deletes all data)
docker-compose down -v

# Complete cleanup (removes images too)
docker-compose down -v --rmi all
```

## Next Steps

1. Test each part individually
2. Monitor logs to understand the flow
3. Try uploading different files
4. Experiment with the computation features
5. Check how auto-sync works in Part 2

## Support

For detailed information, see [README-Docker.md](README-Docker.md)

Happy containerizing! 🐳
