package com.github.nhh;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Solution for https://practice.geeksforgeeks.org/problems/word-boggle/0
 */
public class WordBoggle implements GfGAlgorithm {

    public static void main(String[] args) throws IOException {
        WordBoggle boggle = new WordBoggle();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int numProblems = Integer.parseInt(reader.readLine());
        for (int i = 0; i < numProblems; i++) {
            List<String> lines = new ArrayList<>();
            for(int j = 0; j < 4; j++) {
                lines.add(reader.readLine());
            }
            List<String> output = boggle.solve(lines);
            output.forEach(System.out::println);
        }
    }

    @Override
    public List<String> solve(List<String> input) {
        Integer numberOfWords = Integer.parseInt(input.get(0));
        String[] words = input.get(1).split(" ");
        String[] gridSize = input.get(2).split(" ");
        Integer numColumns = Integer.parseInt(gridSize[1]);
        Integer numRows = Integer.parseInt(gridSize[0]);
        
        String[] gridAsStrings = input.get(3).split(" ");
        Grid grid = new Grid();
        grid.grid = constructGrid(gridAsStrings, numColumns, numRows);
        grid.numColumns = numColumns;
        grid.numRows = numRows;

        Collection<String> wordList = constructWordList(words, numberOfWords);
        return solve(wordList, grid);
    }

    private Character[][] constructGrid(String[] gridAsStrings, Integer numColumns, Integer numRows) {
        Character[][] result = new Character[numRows][numColumns];
        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numColumns; col++) {
                result[row][col] = gridAsStrings[(row * numColumns) + col].charAt(0);
            }
        }
        return result;
    }

    private List<String> solve(Collection<String> wordList, Grid grid) {
        Map<Character, List<Position>> position = createPositionIndex(grid);
        List<String> matches = new ArrayList<>();
        for (String word: wordList) {
            Character start = word.charAt(0);
            List<Position> positions = position.get(start);
            if (positions != null && !positions.isEmpty()) {
                for(Position startPosition: positions) {
                    int row = startPosition.row;
                    int col = startPosition.column;
                    if (search(row, col, word, 0, grid, new HashSet<>())) {
                        matches.add(word);
                        break;
                    }
                }
            }
        }
        List<String> result =  new ArrayList<>();
        if (matches.isEmpty()) {
            result.add("-1");
        } else {
            matches.sort(String::compareTo);
            result.add(join(" ", matches));
        }
        return result;
    }

    private String join(String delimiter, Collection<String> items) {
        String delim = "";
        StringBuilder builder = new StringBuilder();
        for(String item : items) {
            builder.append(delim);
            delim = delimiter;
            builder.append(item);
        }
        return builder.toString();
    }

    private boolean search(int row, int col, String word, int index, Grid grid, HashSet<Position> history) {
        if (index == word.length()) {
            return true;
        }
        if (row < 0 || col < 0 || row >= grid.numRows || col >= grid.numColumns) {
            // deal with out of bounds
            return false;
        }
        Position current = new Position(row, col);
        if (history.contains(current)) {
            return false;
        }
        char character = word.charAt(index);
        char gridCharacter = grid.grid[row][col];
        if (character == gridCharacter) {
            HashSet<Position> copied = copyAndAppend(history, current);
            return search(row+1, col, word, index+1, grid, copied) ||
                   search(row+1, col+1, word, index+1, grid, copied) ||
                   search(row, col+1, word, index+1, grid, copied) ||
                   search(row-1, col+1, word, index+1, grid, copied) ||
                   search(row+1, col-1, word, index+1, grid,copied) ||
                   search(row-1, col-1, word, index+1, grid, copied) ||
                   search(row-1, col, word, index+1, grid, copied) ||
                   search(row, col-1, word, index+1, grid, copied);
        } else {
            return false;
        }
    }

    private HashSet<Position> copyAndAppend(HashSet<Position> history, Position current) {
        HashSet<Position> result = new HashSet<>(history);
        result.add(current);
        return result;

    }

    private Map<Character, List<Position>> createPositionIndex(Grid grid) {
        Map<Character, List<Position>> result = new HashMap<>();
        for(int row = 0; row < grid.numRows; row++) {
            for (int col = 0; col < grid.numColumns; col++) {
                Character character = grid.grid[row][col];
                List<Position> positions = result.computeIfAbsent(character, k -> new LinkedList<>());
                positions.add(new Position(row, col));
            }
        }
        return result;
    }

    private Set<String> constructWordList(String[] words, Integer numberOfWords) {
        Set<String> wordList = new HashSet<>(words.length);
        int count = 0;
        for(String word: words) {
            if (count < numberOfWords) {
                wordList.add(word);
            } else {
                break;
            }
            count++;
        }
        return wordList;
    }
    
    private class Position {
        private int row;
        private int column;

        public Position(int row, int col) {
            this.row = row;
            this.column = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return row == position.row &&
                    column == position.column;
        }

        @Override
        public int hashCode() {

            return Objects.hash(row, column);
        }
    }

    private class Grid {
        Character [][] grid;
        int numRows;
        int numColumns;
    }
}
