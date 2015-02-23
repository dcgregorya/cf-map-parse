package org.cf.maps

import groovy.xml.MarkupBuilder;

public class Parser {
    String content;
    def map;

    public Parser(String fName) {
        def file = new File(fName);

        if (file != null &&
                file.canRead()) {
            content = file.text;
        } else {
            throw new Exception("Unable to read file : $fName");
        }
    }

    def parse() {
        def y = 0;
        map = [];

        def done = false;
        content.eachLine { line ->

            if(line.matches(".*END.*")) {
                done = true;
            } else if (!done) {
                line.chars.eachWithIndex { char c, int i ->
                    def type = 'Empty';
                    if (c == '+') {
                        type = 'Room';
                    } else if (c == '-') {
                        type = 'ExitX';
                    } else if (c == '|') {
                        type = 'ExitY';
                    } else if (c.letterOrDigit) {
                        type = 'Special';
                    }

                    map << [x: i, y: y, type: type, mark: c];
                };
            }

            y++;
        };
    }

    def getHTML() {
        def sw = new StringWriter();
        def page = new MarkupBuilder(sw);
        def rect = new StringWriter();

        if (map) {
            map.each {
                def x = it.x;
                def y = it.y;

                if(it.type == "Room" || it.type == "Special") {
                    def fill = "#eeeeee";

                    if(it.type == "Special") {
                        fill = "#eeeedd";
                    }

                    rect.append("""
                        var rect_X${x}_Y${y} = new Kinetic.Rect({
                            x: ${10 * (x + 1)},
                            y: ${10 * (y + 1)},
                            width: 10,
                            height: 10,
                            offset : {x:0, y:0},
                            fill: '$fill'
                        });
                        layer.add(rect_X${x}_Y${y});
                    """);
                }
            }
        }

        def js = """
            var stage = new Kinetic.Stage({
                container: "container",
                width: 1024,
                height: 768
                });

            var layer = new Kinetic.Layer();

            ${rect.toString()}

            stage.add(layer);
        """;

        page.html {
            head {
                title("Demo Map");
            }
            body {
                div(id:"container");
                script(src: "js/kinetic-v5.1.0.js", "");
                script(type: "text/javascript", js);
            }
        }

        sw.toString();
    }
}
