#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}  GDrive RMI Application Launcher${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_usage() {
    print_header
    echo ""
    echo "Usage: $0 <part> <role> [server_host]"
    echo ""
    echo "Arguments:"
    echo "  part          : part1 | part2 | part3"
    echo "  role          : server | client"
    echo "  server_host   : hostname/IP of RMI server (default: localhost)"
    echo ""
    echo "Examples:"
    echo "  $0 part1 server"
    echo "  $0 part1 client part1-server"
    echo "  $0 part2 server"
    echo "  $0 part3 client part3-server"
    echo ""
    echo "Part Descriptions:"
    echo "  part1  : File Server with Manual Operations"
    echo "           - Server: Provides file upload, delete, rename"
    echo "           - Client: Interactive menu-based operations"
    echo ""
    echo "  part2  : Automatic File Synchronization System"
    echo "           - Server: Timestamp-based file management"
    echo "           - Client: Automatic file system monitoring"
    echo ""
    echo "  part3  : Computation Server"
    echo "           - Server: Addition and array sorting operations"
    echo "           - Client: Interactive computation requests"
    echo ""
}

wait_for_rmi() {
    local max_attempts=30
    local attempt=1

    echo -e "${YELLOW}Waiting for RMI registry to be ready...${NC}"

    while [ $attempt -le $max_attempts ]; do
        if nc -z localhost 1099 2>/dev/null; then
            echo -e "${GREEN}RMI registry is ready!${NC}"
            sleep 2  # Additional wait for binding
            return 0
        fi
        echo -e "Attempt $attempt/$max_attempts..."
        sleep 2
        attempt=$((attempt + 1))
    done

    echo -e "${RED}RMI registry failed to start!${NC}"
    return 1
}

wait_for_server() {
    local server_host=$1
    local max_attempts=30
    local attempt=1

    echo -e "${YELLOW}Waiting for server at ${server_host}:1099...${NC}"

    while [ $attempt -le $max_attempts ]; do
        if nc -z "$server_host" 1099 2>/dev/null; then
            echo -e "${GREEN}Server is ready!${NC}"
            sleep 2  # Additional wait for RMI binding
            return 0
        fi
        echo -e "Attempt $attempt/$max_attempts..."
        sleep 2
        attempt=$((attempt + 1))
    done

    echo -e "${RED}Server is not reachable!${NC}"
    return 1
}

launch_part1_server() {
    print_header
    echo -e "${GREEN}Starting Part 1 Server: File Server with Manual Operations${NC}"
    echo -e "Listening on port: 1099"
    echo -e "Storage directory: /app/Part1/server_uploads"
    echo ""

    cd /app/Part1
    exec java -Djava.rmi.server.hostname=${JAVA_RMI_SERVER_HOSTNAME:-localhost} \
              -Djava.rmi.server.codebase=file:/app/Part1/ \
              FileServerImpl
}

launch_part1_client() {
    local server_host=${1:-localhost}

    print_header
    echo -e "${GREEN}Starting Part 1 Client: File Operations Client${NC}"
    echo -e "Connecting to server: $server_host:1099"
    echo ""

    wait_for_server "$server_host"

    cd /app/Part1
    exec java -Djava.rmi.server.hostname=$server_host \
              FileClientWrapper "$server_host"
}

launch_part2_server() {
    print_header
    echo -e "${GREEN}Starting Part 2 Server: File Synchronization Server${NC}"
    echo -e "Listening on port: 1099"
    echo -e "Storage directory: /app/Part2/server_uploads"
    echo ""

    cd /app/Part2
    exec java -Djava.rmi.server.hostname=${JAVA_RMI_SERVER_HOSTNAME:-localhost} \
              -Djava.rmi.server.codebase=file:/app/Part2/ \
              FileServerImpl
}

launch_part2_client() {
    local server_host=${1:-localhost}

    print_header
    echo -e "${GREEN}Starting Part 2 Client: Auto-Sync Client${NC}"
    echo -e "Connecting to server: $server_host:1099"
    echo -e "Watching directory: /app/Part2/client_sync"
    echo ""

    wait_for_server "$server_host"

    cd /app/Part2
    mkdir -p client_sync
    exec java -Djava.rmi.server.hostname=$server_host \
              FileClientWrapper "$server_host"
}

launch_part3_server() {
    print_header
    echo -e "${GREEN}Starting Part 3 Server: Computation Server${NC}"
    echo -e "Listening on port: 1099"
    echo ""

    cd /app/Part3
    exec java -Djava.rmi.server.hostname=${JAVA_RMI_SERVER_HOSTNAME:-localhost} \
              -Djava.rmi.server.codebase=file:/app/Part3/ \
              Server
}

launch_part3_client() {
    local server_host=${1:-localhost}

    print_header
    echo -e "${GREEN}Starting Part 3 Client: Computation Client${NC}"
    echo -e "Connecting to server: $server_host:1099"
    echo ""

    wait_for_server "$server_host"

    cd /app/Part3
    exec java -Djava.rmi.server.hostname=$server_host \
              ClientWrapper "$server_host"
}

# Main script logic
PART=$1
ROLE=$2
SERVER_HOST=${3:-localhost}

if [ "$PART" = "help" ] || [ -z "$PART" ] || [ -z "$ROLE" ]; then
    print_usage
    exit 0
fi

case "$PART" in
    part1)
        case "$ROLE" in
            server) launch_part1_server ;;
            client) launch_part1_client "$SERVER_HOST" ;;
            *) print_usage; exit 1 ;;
        esac
        ;;
    part2)
        case "$ROLE" in
            server) launch_part2_server ;;
            client) launch_part2_client "$SERVER_HOST" ;;
            *) print_usage; exit 1 ;;
        esac
        ;;
    part3)
        case "$ROLE" in
            server) launch_part3_server ;;
            client) launch_part3_client "$SERVER_HOST" ;;
            *) print_usage; exit 1 ;;
        esac
        ;;
    *)
        echo -e "${RED}Error: Invalid part '$PART'${NC}"
        print_usage
        exit 1
        ;;
esac