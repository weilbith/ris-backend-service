package de.bund.digitalservice.ris.domain.docx;

import java.util.List;
import java.util.stream.Collectors;

public class DocUnitTableCellElement implements DocUnitDocx {
  public List<DocUnitDocx> paragraphElements;

  public final BlockBorder border;

  public DocUnitTableCellElement(List<DocUnitDocx> paragraphElements, BlockBorder border) {
    this.paragraphElements = paragraphElements;
    this.border = border != null ? border : new BlockBorder();
  }

  public DocUnitTableCellElement(List<DocUnitDocx> paragraphElements) {
    this.paragraphElements = paragraphElements;
    this.border = new BlockBorder();
  }

  private boolean hasStyle() {
    return border.isSet();
  }

  @Override
  public String toHtmlString() {
    StringBuilder sb = new StringBuilder("<td");

    if (hasStyle()) {
      sb.append(" style=\"");
      sb.append(border.toHtmlString());
      sb.append("\"");
    }
    sb.append(">");
    sb.append(
        paragraphElements.stream().map(DocUnitDocx::toHtmlString).collect(Collectors.joining()));
    sb.append("</td>");

    return sb.toString();
  }
}
