JAVAC = javac
JAVA = java
SRC = Screen.java Cards.java ImagePanel.java bucky.java
CLASS_FILES = $(SRC:.java=.class)

all: $(CLASS_FILES)

%.class: %.java
	$(JAVAC) $<

run: all
	$(JAVA) bucky

clean:
	rm -f *.class
