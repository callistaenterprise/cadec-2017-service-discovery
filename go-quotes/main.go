package main

import (
        "fmt"
)

var appName = "quote-service"

func main() {
        fmt.Printf("Starting ML Go version of %v on port 8080\n", appName)
        MLStartWebServer("8080")
}
