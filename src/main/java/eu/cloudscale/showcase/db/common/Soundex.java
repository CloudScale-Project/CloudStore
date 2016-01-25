/*******************************************************************************
*  Copyright (c) 2015 XLAB d.o.o.
*  All rights reserved. This program and the accompanying materials
*  are made available under the terms of the Eclipse Public License v1.0
*  which accompanies this distribution, and is available at
*  http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/
package eu.cloudscale.showcase.db.common;

public class Soundex
{

	public static String soundex(String s)
	{
		char[] x = s.toUpperCase().toCharArray();
		char firstLetter = x[0];

		// convert letters to numeric code
		for ( int i = 0; i < x.length; i++ )
		{
			switch ( x[i] )
			{
				case 'B':
				case 'F':
				case 'P':
				case 'V':
				{
					x[i] = '1';
					break;
				}

				case 'C':
				case 'G':
				case 'J':
				case 'K':
				case 'Q':
				case 'S':
				case 'X':
				case 'Z':
				{
					x[i] = '2';
					break;
				}

				case 'D':
				case 'T':
				{
					x[i] = '3';
					break;
				}

				case 'L':
				{
					x[i] = '4';
					break;
				}

				case 'M':
				case 'N':
				{
					x[i] = '5';
					break;
				}

				case 'R':
				{
					x[i] = '6';
					break;
				}

				default:
				{
					x[i] = '0';
					break;
				}
			}
		}

		// remove duplicates
		String output = "" + firstLetter;
		for ( int i = 1; i < x.length; i++ )
			if ( x[i] != x[i - 1] && x[i] != '0' )
				output += x[i];

		// pad with 0's or truncate
		output = output + "0000";
		return output.substring( 0, 4 );
	}

	public static void main(String[] args)
	{
		String name1 = args[0];
		String name2 = args[1];
		String code1 = soundex( name1 );
		String code2 = soundex( name2 );
		System.out.println( code1 + ": " + name1 );
		System.out.println( code2 + ": " + name2 );
	}
}
