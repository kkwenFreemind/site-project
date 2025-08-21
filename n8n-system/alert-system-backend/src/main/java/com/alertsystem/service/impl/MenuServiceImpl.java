package com.alertsystem.service.impl;

import com.alertsystem.dto.menu.*;
import com.alertsystem.entity.Menu;
import com.alertsystem.entity.Role;
import com.alertsystem.exception.ResourceNotFoundException;
import com.alertsystem.exception.BusinessException;
import com.alertsystem.repository.MenuRepository;
import com.alertsystem.repository.RoleRepository;
import com.alertsystem.repository.UserRepository;
import com.alertsystem.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public MenuDTO createMenu(MenuCreateRequest request) {
        log.info("Creating menu: {}", request.getName());
        
        // 檢查父選單是否存在
        if (request.getParentId() != null && request.getParentId() > 0) {
            menuRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("父選單不存在"));
        }
        
        // 檢查同級選單名稱是否重複
        List<Menu> siblings = menuRepository.findByParentIdOrderBySortOrderAsc(
            request.getParentId() != null ? request.getParentId() : 0L);
        boolean nameExists = siblings.stream()
            .anyMatch(menu -> menu.getName().equals(request.getName()));
        if (nameExists) {
            throw new BusinessException("同級選單名稱已存在");
        }
        
        Menu menu = convertToEntity(request);
        menu = menuRepository.save(menu);
        
        log.info("Menu created successfully with id: {}", menu.getId());
        return convertToDTO(menu);
    }

    @Override
    public MenuDTO updateMenu(MenuUpdateRequest request) {
        log.info("Updating menu: {}", request.getId());
        
        Menu menu = menuRepository.findById(request.getId())
            .orElseThrow(() -> new ResourceNotFoundException("選單不存在"));
        
        // 檢查是否將選單設為自己的子選單
        if (request.getParentId() != null && request.getParentId().equals(request.getId())) {
            throw new BusinessException("不能將選單設為自己的子選單");
        }
        
        updateEntity(menu, request);
        menu = menuRepository.save(menu);
        
        log.info("Menu updated successfully: {}", menu.getId());
        return convertToDTO(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        log.info("Deleting menu: {}", id);
        
        Menu menu = menuRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("選單不存在"));
        
        // 檢查是否有子選單
        if (hasChildren(id)) {
            throw new BusinessException("存在子選單，無法刪除");
        }
        
        menuRepository.delete(menu);
        log.info("Menu deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuDTO getMenuById(Long id) {
        Menu menu = menuRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("選單不存在"));
        return convertToDTO(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDTO> getAllMenuTree() {
        List<Menu> allMenus = menuRepository.findAllVisibleMenus();
        return buildMenuTree(allMenus);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuDTO> getMenuList(Pageable pageable) {
        Page<Menu> menuPage = menuRepository.findAll(pageable);
        return menuPage.map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserMenuTree> getUserMenuTree(Long userId) {
        log.info("Getting user menu tree for user: {}", userId);
        
        // 驗證用戶是否存在
        userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("用戶不存在"));
        
        List<Menu> userMenus = menuRepository.findMenuTreeByUserId(userId);
        return buildUserMenuTree(userMenus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserPermissions(Long userId) {
        return menuRepository.findPermissionsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDTO> getMenusByRoleId(Long roleId) {
        List<Menu> menus = menuRepository.findMenusByRoleId(roleId);
        return menus.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
        log.info("Assigning menus to role: {}, menus: {}", roleId, menuIds);
        
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("角色不存在"));
        
        Set<Menu> menus = new HashSet<>();
        if (menuIds != null && !menuIds.isEmpty()) {
            menus = menuIds.stream()
                .map(menuId -> menuRepository.findById(menuId)
                    .orElseThrow(() -> new ResourceNotFoundException("選單不存在: " + menuId)))
                .collect(Collectors.toSet());
        }
        
        role.setMenus(menus);
        roleRepository.save(role);
        
        log.info("Menus assigned to role successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDTO> getChildMenus(Long parentId) {
        List<Menu> children = menuRepository.findByParentIdOrderBySortOrderAsc(parentId);
        return children.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuDTO> getMenusByType(Integer type) {
        List<Menu> menus = menuRepository.findByTypeOrderBySortOrderAsc(type);
        return menus.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasChildren(Long menuId) {
        return menuRepository.existsByParentId(menuId);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuDTO getMenuByPermission(String permission) {
        Menu menu = menuRepository.findByPermission(permission);
        return menu != null ? convertToDTO(menu) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public MenuDTO getMenuByRouteName(String routeName) {
        Menu menu = menuRepository.findByRouteName(routeName);
        return menu != null ? convertToDTO(menu) : null;
    }

    @Override
    public List<MenuDTO> buildMenuTree(List<Menu> menus) {
        Map<Long, MenuDTO> menuMap = new HashMap<>();
        List<MenuDTO> rootMenus = new ArrayList<>();
        
        // 轉換為DTO並建立映射
        for (Menu menu : menus) {
            MenuDTO menuDTO = convertToDTO(menu);
            menuMap.put(menu.getId(), menuDTO);
        }
        
        // 構建樹狀結構
        for (Menu menu : menus) {
            MenuDTO menuDTO = menuMap.get(menu.getId());
            if (menu.getParentId() == null || menu.getParentId() == 0L) {
                rootMenus.add(menuDTO);
            } else {
                MenuDTO parentDTO = menuMap.get(menu.getParentId());
                if (parentDTO != null) {
                    if (parentDTO.getChildren() == null) {
                        parentDTO.setChildren(new ArrayList<>());
                    }
                    parentDTO.getChildren().add(menuDTO);
                }
            }
        }
        
        // 對子選單進行排序
        sortMenuTree(rootMenus);
        
        return rootMenus;
    }

    private void sortMenuTree(List<MenuDTO> menus) {
        if (menus == null || menus.isEmpty()) {
            return;
        }
        
        menus.sort(Comparator.comparing(MenuDTO::getSortOrder));
        
        for (MenuDTO menu : menus) {
            if (menu.getChildren() != null) {
                sortMenuTree(menu.getChildren());
            }
        }
    }

    private List<UserMenuTree> buildUserMenuTree(List<Menu> menus) {
        Map<Long, UserMenuTree> menuMap = new HashMap<>();
        List<UserMenuTree> rootMenus = new ArrayList<>();
        
        // 轉換為UserMenuTree並建立映射
        for (Menu menu : menus) {
            UserMenuTree userMenu = convertToUserMenuTree(menu);
            menuMap.put(menu.getId(), userMenu);
        }
        
        // 構建樹狀結構
        for (Menu menu : menus) {
            UserMenuTree userMenu = menuMap.get(menu.getId());
            if (menu.getParentId() == null || menu.getParentId() == 0L) {
                rootMenus.add(userMenu);
            } else {
                UserMenuTree parentMenu = menuMap.get(menu.getParentId());
                if (parentMenu != null) {
                    if (parentMenu.getChildren() == null) {
                        parentMenu.setChildren(new ArrayList<>());
                    }
                    parentMenu.getChildren().add(userMenu);
                }
            }
        }
        
        // 對子選單進行排序
        sortUserMenuTree(rootMenus);
        
        return rootMenus;
    }

    private void sortUserMenuTree(List<UserMenuTree> menus) {
        if (menus == null || menus.isEmpty()) {
            return;
        }
        
        for (UserMenuTree menu : menus) {
            if (menu.getChildren() != null) {
                sortUserMenuTree(menu.getChildren());
            }
        }
    }

    private UserMenuTree convertToUserMenuTree(Menu menu) {
        UserMenuTree.MenuMeta meta = UserMenuTree.MenuMeta.builder()
            .title(menu.getName())
            .icon(menu.getIcon())
            .hidden(!menu.isVisible())
            .keepAlive(menu.isKeepAlive())
            .permission(menu.getPermission())
            .build();
        
        return UserMenuTree.builder()
            .id(menu.getId())
            .name(menu.getRouteName())
            .path(menu.getRoutePath())
            .component(menu.getComponent())
            .redirect(menu.getRedirect())
            .alwaysShow(menu.isAlwaysShow())
            .meta(meta)
            .build();
    }

    @Override
    public MenuDTO convertToDTO(Menu menu) {
        if (menu == null) {
            return null;
        }
        
        return MenuDTO.builder()
            .id(menu.getId())
            .parentId(menu.getParentId())
            .treePath(menu.getTreePath())
            .name(menu.getName())
            .type(menu.getType())
            .routeName(menu.getRouteName())
            .routePath(menu.getRoutePath())
            .component(menu.getComponent())
            .permission(menu.getPermission())
            .alwaysShow(menu.getAlwaysShow())
            .keepAlive(menu.getKeepAlive())
            .visible(menu.getVisible())
            .sortOrder(menu.getSortOrder())
            .icon(menu.getIcon())
            .redirect(menu.getRedirect())
            .params(menu.getParams())
            .createdAt(menu.getCreatedAt())
            .updatedAt(menu.getUpdatedAt())
            .typeDescription(getTypeDescription(menu.getType()))
            .hasChildren(hasChildren(menu.getId()))
            .build();
    }

    private String getTypeDescription(Integer type) {
        if (type == null) {
            return "";
        }
        try {
            return Menu.MenuType.fromValue(type).getDescription();
        } catch (IllegalArgumentException e) {
            return "未知";
        }
    }

    @Override
    public Menu convertToEntity(MenuCreateRequest request) {
        return Menu.builder()
            .parentId(request.getParentId() != null ? request.getParentId() : 0L)
            .name(request.getName())
            .type(request.getType())
            .routeName(request.getRouteName())
            .routePath(request.getRoutePath())
            .component(request.getComponent())
            .permission(request.getPermission())
            .alwaysShow(request.getAlwaysShow() != null ? request.getAlwaysShow() : 0)
            .keepAlive(request.getKeepAlive() != null ? request.getKeepAlive() : 0)
            .visible(request.getVisible() != null ? request.getVisible() : 1)
            .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
            .icon(request.getIcon())
            .redirect(request.getRedirect())
            .params(request.getParams())
            .build();
    }

    @Override
    public void updateEntity(Menu menu, MenuUpdateRequest request) {
        if (request.getParentId() != null) {
            menu.setParentId(request.getParentId());
        }
        if (request.getName() != null) {
            menu.setName(request.getName());
        }
        if (request.getType() != null) {
            menu.setType(request.getType());
        }
        if (request.getRouteName() != null) {
            menu.setRouteName(request.getRouteName());
        }
        if (request.getRoutePath() != null) {
            menu.setRoutePath(request.getRoutePath());
        }
        if (request.getComponent() != null) {
            menu.setComponent(request.getComponent());
        }
        if (request.getPermission() != null) {
            menu.setPermission(request.getPermission());
        }
        if (request.getAlwaysShow() != null) {
            menu.setAlwaysShow(request.getAlwaysShow());
        }
        if (request.getKeepAlive() != null) {
            menu.setKeepAlive(request.getKeepAlive());
        }
        if (request.getVisible() != null) {
            menu.setVisible(request.getVisible());
        }
        if (request.getSortOrder() != null) {
            menu.setSortOrder(request.getSortOrder());
        }
        if (request.getIcon() != null) {
            menu.setIcon(request.getIcon());
        }
        if (request.getRedirect() != null) {
            menu.setRedirect(request.getRedirect());
        }
        if (request.getParams() != null) {
            menu.setParams(request.getParams());
        }
    }
}
