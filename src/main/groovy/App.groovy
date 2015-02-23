import org.cf.maps.Parser;

def mapFolder="D:\\Dev\\cf-map-parser"
def map = "akanMap.txt"

Parser p = new Parser("$mapFolder\\$map");
p.parse();
def html = p.getHTML();

print html;
