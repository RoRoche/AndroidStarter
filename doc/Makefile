INPUT=architecture.md
OUTPUT=architecture.pdf
LISTINGS=listings_setup.tex

$(OUTPUT): $(INPUT)
	pandoc $(INPUT) -N --template=template.tex --variable documentclass="scrartcl" --variable urlcolor="LimeGreen" --variable fontfamily="lmodern" --latex-engine=xelatex --listings -H $(LISTINGS) -o $(OUTPUT)

run: $(OUTPUT)
	open $(OUTPUT)

clean:
	rm -f $(OUTPUT)
