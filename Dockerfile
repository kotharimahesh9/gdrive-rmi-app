# Multi-stage build for Java RMI Application
FROM openjdk:21-slim AS build

# Set working directory
WORKDIR /app

# Copy all source files
COPY Part1/ ./Part1/
COPY Part2/ ./Part2/
COPY Part3/ ./Part3/

# Compile all parts
RUN cd Part1 && javac *.java && \
    cd ../Part2 && javac *.java && \
    cd ../Part3 && javac *.java

# Runtime stage
FROM openjdk:21-slim

# Install necessary utilities
RUN apt-get update && apt-get install -y \
    curl \
    netcat-openbsd \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy compiled classes from build stage
COPY --from=build /app/Part1/*.class ./Part1/
COPY --from=build /app/Part2/*.class ./Part2/
COPY --from=build /app/Part3/*.class ./Part3/

# Create necessary directories first
RUN mkdir -p Part1/server_uploads Part1/clientFiles \
    Part2/server_uploads Part2/client_sync \
    Part3/data

# Copy client files and handle empty directories gracefully
COPY --chown=root:root Part1/clientFiles ./Part1/clientFiles/
COPY --chown=root:root Part1/server_uploads ./Part1/server_uploads/

# Copy launcher script and fix line endings
COPY launcher.sh /app/launcher.sh
RUN sed -i 's/\r$//' /app/launcher.sh && chmod +x /app/launcher.sh

# Expose RMI port
EXPOSE 1099

# Default entry point
ENTRYPOINT ["/app/launcher.sh"]
CMD ["help"]