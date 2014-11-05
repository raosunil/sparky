//
//  ViewController2.m
//  Mayo-ipad
//
//  Created by Nagesh Kumar Uba on 08/10/14.
//  Copyright (c) 2014 Nagesh Kumar Uba. All rights reserved.
//

#import "TableViewController.h"
#import "TableCell.h"

@interface TableViewController()

@end

@implementation TableViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self.view setBackgroundColor:[UIColor clearColor]];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    
    _Title = @[@"FirstName",
               @"LastName",
               @"personId",
               @"lanId",];
    
    /*_Description = @[userMap[@"firstName"],
                     userMap[@"lastname"],
                     userMap[@"personId"],
                     userMap[@"lanId"],];*/
    _Description = @[@"Sunil",
                     @"Rao",
                     @"16173198",
                     @"M137121",];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return _Title.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"TableCell";
    TableCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier           forIndexPath:indexPath];
    
    // Configure the cell...
    NSInteger row = [indexPath row];
    
    cell.TitleLabel.text = _Title[row];
    cell.DescriptionLabel.text = _Description[row];
    //cell.ThumbImage.image = [UIImage imageNamed:_Images[row]];;
    
    return cell;
}

@end