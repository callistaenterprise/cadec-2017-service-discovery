package main

import (
	"net/http"
	"encoding/json"
	"strconv"
        "fmt"
	"math/rand"
	"os"
	"net"
)

var  quotes = [...]string{
	"I like a lot of the design decisions they made in the [Go] language. Basically, I like all of them.",
	"In Go, the code does exactly what it says on the page.",
	"Go doesn’t implicitly anything.",
	"If I had to describe Go with one word it’d be ‘sensible’.",
	"Go will be the server language of the future."}

func GetQuote(w http.ResponseWriter, r *http.Request) {

	hostname, _ := os.Hostname()
	addrs, _ := net.LookupHost(hostname)
	addr := ""
	for _, a := range addrs {
		addr = addr + a
	}

	idx := rand.Intn(len(quotes))

	fmt.Printf("Will pick no# %v of the %v quotes\n", idx, len(quotes))
	quote := quotes[idx]
	quoteObject := Quote{hostname + "/" + addr, quote, "EN"}

        // If found, marshal into JSON, write headers and content
	data, _ := json.Marshal(quoteObject)

	fmt.Printf("ML Return string %v\n", string(data))
	w.Header().Set("Content-Type", "application/json")
	w.Header().Set("Content-Length", strconv.Itoa(len(data)))
	w.WriteHeader(http.StatusOK)
	w.Write(data)
}