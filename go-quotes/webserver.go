package main

import (
        "net/http"
        "log"
)

func MLStartWebServer(port string) {

        r := MLNewRouter()
        http.Handle("/", r)

        log.Println("Starting ML HTTP service at " + port)
        err := http.ListenAndServe(":" + port, nil)

        if err != nil {
                log.Println("An error occured starting HTTP listener at port " + port)
                log.Println("Error: " + err.Error())
        }
}
