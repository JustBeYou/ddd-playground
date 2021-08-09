package ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ApplicationKeyboardInput implements ApplicationInput {
  BufferedReader reader;

  public ApplicationKeyboardInput() {
    reader = new BufferedReader(new InputStreamReader(System.in));
  }

  @Override
  public String readLine() throws IOException {
    return reader.readLine();
  }
}
