import wave
import math
import struct
import os

def generate_beep(filename, duration=0.2, freq=880, volume=0.5):
    # Ensure directory exists
    os.makedirs(os.path.dirname(filename), exist_ok=True)
    
    sample_rate = 44100
    n_samples = int(sample_rate * duration)
    
    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)  # Mono
        wav_file.setsampwidth(2)  # 2 bytes per sample (16-bit)
        wav_file.setframerate(sample_rate)
        
        for i in range(n_samples):
            value = int(math.sin(2 * math.pi * freq * i / sample_rate) * volume * 32767)
            data = struct.pack('<h', value)
            wav_file.writeframes(data)
    
    print(f"Generated {filename}")

if __name__ == "__main__":
    generate_beep("assets/sounds/buy.wav", freq=1200, duration=0.15)
    generate_beep("assets/sounds/cursor.wav", freq=600, duration=0.05)
