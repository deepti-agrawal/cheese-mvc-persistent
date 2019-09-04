package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping("")
    public String index(Model model){
        model.addAttribute("title", "Menu List");
        model.addAttribute("menus",menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
    public String addItem(@PathVariable int menuId, Model model){
        Menu menu = menuDao.findOne(menuId);
        AddMenuItemForm form = new AddMenuItemForm(menu,cheeseDao.findAll());
        model.addAttribute("menu",menu);
        model.addAttribute("form",form);
        model.addAttribute("title","Add item to menu: "+menu.getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.POST)
    public String addItem(@PathVariable int menuId, @ModelAttribute @Valid AddMenuItemForm menuItemForm, Errors errors){
        if(errors.hasErrors()){
            return "menu/add-item";
        }
        Cheese cheese = cheeseDao.findOne(menuItemForm.getCheeseId());
        Menu menu = menuDao.findOne(menuItemForm.getMenuId());
        menu.addItem(cheese);
        menuDao.save(menu);
        return ""+"redirect:/menu/view/"+menu.getId();
    }

    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(@PathVariable int menuId, Model model){
        Menu menu = menuDao.findOne(menuId);
        model.addAttribute("title",menu.getName());
        model.addAttribute("menu",menu);
        return "menu/view";
    }

    @RequestMapping(value = "add",method= RequestMethod.GET)
    public String add(Model model){
        model.addAttribute("title","Add Menu");
        model.addAttribute("menu",new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method= RequestMethod.POST)
    public String add(Model model, @ModelAttribute @Valid Menu menu, Errors errors){
        if(errors.hasErrors()){
            return "menu/add";
        }
        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }
}
