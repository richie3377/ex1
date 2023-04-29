package com.example.ex1.Models;

public interface TimerCallback {
    public void OnTick(long milisUntilFinished);
    public void OnTimeout();

}
