package re.jag.parquet.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.arguments.MessageArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import jdk.internal.jline.internal.Nullable;

public class Calculator {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> r = literal("r").executes( (c) -> help(c.getSource()) ).then(
				argument("expression", MessageArgumentType.message()).executes((c) -> calc(c.getSource(), MessageArgumentType.getMessage(c, "expression")))
		);

		dispatcher.register(r);
	}

	private static int calc(ServerCommandSource _source, Text _expression) {
		String expression = _expression.asString();
		expression = expression.replace(" ", "");
		char expression_array[] = expression.toCharArray();
		ArrayList<String> parts = new ArrayList<String>();
		ArrayList<String> operators = new ArrayList<String>();

		int last_break=-1;
		String element="";
		for(int i = 0; i < expression_array.length; i++) {
			int op_weight = get_operator_weight(expression_array[i]);

			if (Math.abs(last_break - i) > 1 && op_weight > 0) {
				parts.add(element);
				operators.add(String.copyValueOf(expression_array, i, 1));

				element = "";
				last_break = i;
			} else {
				element+=expression_array[i];
			}
		}

		if(element.length() > 0)
			parts.add(element);

		int current_level = 2;
		while(current_level > 0) {
			int level_count = 0;
			for (int o = 0; o < operators.size(); o++) {
				String operator = operators.get(o);
				if (get_operator_weight(operator) == current_level) {
					level_count++;

					String result = execute_operation(operator, parts.get(o), parts.get(o+1), _source);
					parts.set(o, result);
					parts.remove(o+1);
					operators.remove(o);
					break;
				}
			}

			if (level_count == 0)
				current_level--;
		}

		_source.sendFeedback(new LiteralText(parts.get(0)), false);

		return 0;
	}

	private static Integer parse_operand(String _operand, ServerCommandSource _source) {
		Integer operand = null;

		try {
			operand = Integer.parseInt(_operand);
		} catch (NumberFormatException e) {
			Entity player = _source.getEntity();
			switch (_operand) {
				case "x":
					if (player != null)
						operand = (int)player.getPos().x;
					break;
				case "y":
					if (player != null)
						operand = (int)player.getPos().y;
					break;
				case "z":
					if (player != null)
						operand = (int)player.getPos().z;
					break;
				default:
			}
		}

		if (operand == null) {
			String msg = "Parsing failed for Operand \"" + _operand + "\"";
			_source.sendError(new LiteralText(msg));
		}

		return operand;
	}

	private static int get_operator_weight(char _c) {
		switch(_c) {
			case '+':
			case '-':
				return 1;
			case '*':
			case '/':
				return 2;
		}
		return 0;
	}

	private static int get_operator_weight(String _s) {
		if (_s.length() != 1)
			return 0;
		return get_operator_weight(_s.toCharArray()[0]);
	}

	private static String execute_operation(String _operator, String _left, String _right, ServerCommandSource _source) {
		String result = null;

		Integer left = parse_operand(_left, _source);
		Integer right = parse_operand(_right, _source);

		if (left == null || right == null)
			return null;

		switch (_operator) {
			case "+":
				result = String.valueOf(left + right);
				break;
			case "-":
				result = String.valueOf(left - right);
				break;
			case "*":
				result = String.valueOf(left * right);
				break;
			case "/":
				result = String.valueOf(left / right);
				break;
		}

		return result;
	}

	private static int help(ServerCommandSource _source){
		_source.sendFeedback(new LiteralText("RTFM"), false);
		return 0;
	}
}
