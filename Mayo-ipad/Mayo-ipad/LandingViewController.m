//
//  ViewController2.m
//  Mayo-ipad
//
//  Created by Nagesh Kumar Uba on 08/10/14.
//  Copyright (c) 2014 Nagesh Kumar Uba. All rights reserved.
//

#import "LandingViewController.h"
#import "TableCell.h"

@interface LandingViewController()

@end

@implementation LandingViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self.view setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"mayobg.png"]]];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    
    _Title = @[@"Sunil",];
    
    /*_Description = @[userMap[@"firstName"],
                     userMap[@"lastname"],
                     userMap[@"personId"],
                     userMap[@"lanId"],];*/
    _Description = @[@"M137121",];
    
    _Banner = @[@"Banner.jpg",];
    [userlabel setText:@"Sunil"];
    [idlabel setText:@"M137121"];

}

- (IBAction)backgroundTap:(id)sender {
    [self.view endEditing:YES];
}

-(BOOL) textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)LogoutButton:(id)sender {
    [self dismissViewControllerAnimated:YES completion:nil];
}
@end