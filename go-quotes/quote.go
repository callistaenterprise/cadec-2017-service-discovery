package main

type Quote struct {
        HardwareArchitecture string `json:"hardwareArchitecture"`
        OperatingSystem string `json:"operatingSystem"`
        IpAddress string `json:"ipAddress"`
        Quote string `json:"quote"`
        Language string `json:"language"`
}

func (q *Quote) ToString() string {
        return q.HardwareArchitecture + " " + q.OperatingSystem + " " + q.IpAddress + " " + q.Language + " " + q.Quote
}