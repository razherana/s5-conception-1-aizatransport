package mg.razherana.aizatransport.configs.sidebar;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SidebarDTO {
  private SidebarDTO parent;
  private boolean menu;
  private String name;
  private String link;
  private String icon;
  private List<SidebarDTO> items = new ArrayList<>();

  public SidebarDTO(SidebarDTO parent, boolean menu, String name, String link, String icon) {
    this.parent = parent;
    this.menu = menu;
    this.name = name;
    this.link = link;
    this.icon = icon;
  }

  public static SidebarDTO createMenuStatic(String name) {
    return new SidebarDTO(null, true, name, null, null);
  }

  public boolean isMenu() {
    return menu;
  }

  public boolean hasParent() {
    return parent != null;
  }

  public SidebarDTO createItem(String name, String link, String icon) {
    SidebarDTO item = new SidebarDTO(this, false, name, link, icon);
    items.add(item);
    return item;
  }

  public SidebarDTO createMenu(String name) {
    SidebarDTO item = new SidebarDTO(this, true, name, null, null);
    items.add(item);
    return item;
  }

  public SidebarDTO createItem(String name, String link, String icon, SidebarDTO[] subItems) {
    SidebarDTO item = new SidebarDTO(this, true, name, link, icon);
    for (SidebarDTO subItem : subItems) {
      subItem.setParent(item);
      item.getItems().add(subItem);
    }
    items.add(item);
    return item;
  }
}
