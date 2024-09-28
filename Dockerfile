# Use the official SBT image
FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0

# Set the working directory in the container
WORKDIR /app

# Copy the project files to the container
COPY . .

# Expose the port the server runs on
EXPOSE 8080

# Run SBT commands
CMD ["sbt", "clean", "compile", "test", "run"]
